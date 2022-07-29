package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import java_cup.internal_error;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticPass extends VisitorAdaptor {
	int printCallCount = 0;
	int varDeclCount = 0;

	Obj currentMethod = null;
	Struct currentType = null;
	Obj currentRecord = null;    

	int formalParametersCnt = 0;
	int recoredFieldsCnt = 0;

	int doWhileCount = 0;

	int nVars;

	boolean mainExists = false;
	boolean errorDetected = false;
	boolean returnFound = false;

	Collection<Obj> formalParamters; 
	ArrayList<Struct> actualParameters = new ArrayList<>();

	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;

		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}

	/*public void visit(VarDecl vardecl){
	varDeclCount++;
    }

    public void visit(PrintStmtNum print) {
	printCallCount++;
	// log.info("Prepoznata print naredba");
    }

    public void visit(PrintStmtNoNum print) {
	printCallCount++;
	// log.info("Prepoznata print naredba");
    }*/

	// fill symbol table

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		Tab.openScope();
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();

		if (!mainExists) {
			report_error("Greska: u programu mora biti deklarisana metoda sa imenom main kao void metoda bez argumenata", null);
		}
	}

	public void visit(VarSingle varSingle) {
		if (Tab.currentScope.findSymbol(varSingle.getVarName()) == null) {
			report_info("Deklarisana promenljiva " + varSingle.getVarName(), varSingle);	    
			Tab.insert(Obj.Var, varSingle.getVarName(), currentType);   
		} else {
			report_error("Greska: ime " + varSingle.getVarName() + " jer vec deklarisano u istom opsegu!", varSingle);
		}	
	}

	public void visit(VarArray varArray) {
		if (Tab.currentScope.findSymbol(varArray.getVarName()) == null) {
			report_info("Deklarisan niz " + varArray.getVarName(), varArray);		
			Tab.insert(Obj.Var, varArray.getVarName(), new Struct(Struct.Array, currentType));
		} else {
			report_error("Greska: ime " + varArray.getVarName() + " jer vec deklarisano u istom opsegu!", varArray);
		}
	}

	public void visit(ConstantNumber constantNumber) {
		if (!currentType.equals(Tab.intType)) {
			report_error("Greska: dodeljena vrednost ne odgovara deklarisanom tipu", constantNumber);
		} else if (Tab.currentScope.findSymbol(constantNumber.getConstName()) == null) {
			report_info("Deklarisana konstanta " + constantNumber.getConstName(), constantNumber);
			Obj constObj = Tab.insert(Obj.Con, constantNumber.getConstName(), currentType);
			constObj.setAdr(constantNumber.getConstValue());
		} else {
			report_error("Greska: ime " + constantNumber.getConstName() + " jer vec deklarisano u istom opsegu!", constantNumber);
		}
	}

	public void visit(ConstantChar constantChar) {
		if (!currentType.equals(Tab.charType)) {
			report_error("Greska: dodeljena vrednost ne odgovara deklarisanom tipu", constantChar);
		} else if (Tab.currentScope.findSymbol(constantChar.getConstName()) == null) {
			report_info("Deklarisana konstanta " + constantChar.getConstName(), constantChar);
			Obj constObj = Tab.insert(Obj.Con, constantChar.getConstName(), currentType);
			constObj.setAdr(constantChar.getConstValue());
		} else {
			report_error("Greska: ime " + constantChar.getConstName() + " jer vec deklarisano u istom opsegu!", constantChar);
		}
	}

	public void visit(ConstantBool constantBool) {
		Obj boolObj = Tab.find("bool");

		if (!currentType.equals(boolObj.getType())) {
			report_error("Greska: dodeljena vrednost ne odgovara deklarisanom tipu", constantBool);
		} else if (Tab.currentScope.findSymbol(constantBool.getConstName()) == null) {
			report_info("Deklarisana konstanta " + constantBool.getConstName(), constantBool);
			Obj constObj = Tab.insert(Obj.Con, constantBool.getConstName(), currentType);
			int value = constantBool.getConstValue() ? 1 : 0;
			constObj.setAdr(value);
		} else {
			report_error("Greska: ime " + constantBool.getConstName() + " jer vec deklarisano u istom opsegu!", constantBool);
		}
	}

	public void visit(Type type) {	
		Obj typeNode = Tab.find(type.getTypeName());
		if(typeNode == Tab.noObj){
			report_error("Greska: nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
			currentType = Tab.noType;
			type.struct = Tab.noType;
		}else{
			if(Obj.Type == typeNode.getKind()){
				currentType = typeNode.getType();
				type.struct = typeNode.getType();
			}else{
				report_error("Greska: ime " + type.getTypeName() + " ne predstavlja tip!", type);
				currentType = Tab.noType;
				type.struct = Tab.noType;
			}
		}
	}

	public void visit(RecordDeclName recordDeclName) {	
		currentRecord = Tab.insert(Obj.Type, recordDeclName.getRecordName(), new Struct(Struct.Class));
		recordDeclName.obj = currentRecord;
		Tab.openScope();	
	}

	public void visit(RecordDecl recordDecl) {
		// currentRecord.getType().setMembers(Tab.currentScope().getLocals());
		currentRecord.setLevel(recoredFieldsCnt);
		Tab.chainLocalSymbols(currentRecord.getType());
		Tab.closeScope();
		currentRecord = null;
		currentType = null;	
		recoredFieldsCnt = 0;
	}    

	public void visit(FieldListSingle fieldListSingle) {
		int kind = 0;
		String msg = "";

		if (currentMethod != null) {
			kind = Obj.Var;
			msg = "Deklarisano promenljiva metode " + fieldListSingle.getVarName();	    
		} else if (currentRecord != null) {
			kind = Obj.Fld;
			msg = "Deklarisano polje " + fieldListSingle.getVarName();
			recoredFieldsCnt++;
		} else {
			// report error
		}

		if (Tab.currentScope.findSymbol(fieldListSingle.getVarName()) == null) {
			report_info(msg, fieldListSingle);	
			Tab.insert(kind, fieldListSingle.getVarName(), currentType);   
		} else {
			report_error("Greska: ime " + fieldListSingle.getVarName() + " jer vec deklarisano u istom opsegu!", fieldListSingle);
		}
	}

	public void visit(FieldListArray fieldListArray) {
		int kind = 0;
		String msg = "";

		if (currentMethod != null) {
			kind = Obj.Var;
			msg = "Deklarisano nizovska promenljiva metode " + fieldListArray.getVarName();	    
		} else if (currentRecord != null) {
			kind = Obj.Fld;
			msg = "Deklarisano nizovsko polje " + fieldListArray.getVarName();
			recoredFieldsCnt++;
		} else {
			// report error
		}

		if (Tab.currentScope.findSymbol(fieldListArray.getVarName()) == null) {
			report_info(msg, fieldListArray);	
			Tab.insert(kind, fieldListArray.getVarName(), new Struct(Struct.Array, currentType));	
		} else {
			report_error("Greska: ime " + fieldListArray.getVarName() + " jer vec deklarisano u istom opsegu!", fieldListArray);
		}
	}

	public void visit(MethodTypeName methodTypeName) {
		// ako je ime vec dekl - postavi fleg da je tako i onda samo ne chainuj na kraju

		currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(), currentType);
		methodTypeName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getMethName(), methodTypeName);
	}

	public void visit(MethodDecl methodDecl) {
		if (currentMethod.getName().equals("main") && 
				currentMethod.getType() == Tab.noType && 
				formalParametersCnt == 0) {	    
			mainExists = true;
		}	    

		if (currentMethod.getType() != Tab.noType && !returnFound) {
			report_error("Greska: semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName() + " nema ispravan return iskaz!", null);
		}

		currentMethod.setLevel(formalParametersCnt);	
		Tab.chainLocalSymbols(currentMethod);	
		Tab.closeScope();
		currentMethod = null;
		currentType = null;
		returnFound = false;
		formalParametersCnt = 0;
	}

	public void visit(ReturnVoidStmt returnVoidStmt) {

	}

	public void visit(ReturnStmt returnStmt) {	
		returnFound = true;

		if (currentMethod == null) {
			report_error("Greska: return naredba ne moze postojati izvan tela funkcija", returnStmt);
		} else if (!returnStmt.getExpr().struct.equals(currentMethod.getType())) {
			report_error("Greska: iskaz return naredne ne odgovara tipu funkcije", returnStmt);
		}
	}       

	public void visit(RetVoid retVoid) {	
		currentType = Tab.noType;	
	}

	public void visit(ParameterSingle parameterSingle) {
		report_info("Parametar funkcije " + currentMethod.getName() + " - " + parameterSingle.getParamName(), 
				parameterSingle);
		formalParametersCnt++;

		if (Tab.currentScope.findSymbol(parameterSingle.getParamName()) == null) {	
			Tab.insert(Obj.Var, parameterSingle.getParamName(), currentType);
		} else {
			report_error("Greska: ime " + parameterSingle.getParamName() + " jer vec deklarisano u istom opsegu!", parameterSingle);
		}
	}

	public void visit(ParameterArray parameterArray) {
		report_info("Nizovski parametar funkcije " + currentMethod.getName() + " - " + parameterArray.getParamName(), 
				parameterArray);
		formalParametersCnt++;

		if (Tab.currentScope.findSymbol(parameterArray.getParamName()) == null) {
			Tab.insert(Obj.Var, parameterArray.getParamName(), currentType);
		} else {
			report_error("Greska: ime " + parameterArray.getParamName() + " jer vec deklarisano u istom opsegu!", parameterArray);
		}
	}

	public void visit(DesignatorIdent designatorIdent) {
		Obj obj = Tab.find(designatorIdent.getDesignatorName());

		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designatorIdent.getLine() + " : ime " + designatorIdent.getDesignatorName() + " nije deklarisano!", null);
		}

		designatorIdent.obj = obj;
	}

	public void visit(DesignatorDot designatorDot) {	
		Obj designatorObj = designatorDot.getDesignator().obj;

		if (designatorObj.getType().getKind() != Struct.Class) {
			report_error("Greska: designator " + designatorDot.getDesignatorName() + " nije objekat ", designatorDot);
		} else {
			boolean found = false;

			for (Obj currObj : designatorObj.getType().getMembers()) {
				if (currObj.getName().equals(designatorDot.getDesignatorName())) {
					found = true;
					designatorDot.obj = currObj;
					break;
				}
			}

			if (found) {
				report_info("Pristup polju " + designatorDot.getDesignatorName() + " klase " + designatorObj.getName(), designatorDot);
			} else {
				report_error("Greska: ne postoji polje " + designatorDot.getDesignatorName() + " u klasi " + designatorObj.getName(), designatorDot);
				designatorDot.obj = Tab.noObj;
			}
		}
	}

	public void visit(DesignatorExpr designatorExpr) {
		Obj designatorObj = designatorExpr.getDesignator().obj;

		if(designatorObj == Tab.noObj) {
			designatorExpr.obj = Tab.noObj;
			return;
		}

		if (designatorExpr.getExpr().struct != Tab.intType) {
			report_error("Greska: indeks niza mora biti tipa int", designatorExpr);
		}	

		if (designatorObj.getType().getKind() == Struct.Array) {
			report_info("Pristup nizu " + designatorObj.getName(), designatorExpr);
			designatorExpr.obj = new Obj(Obj.Elem, "elem", designatorObj.getType().getElemType());	    	    
		} else {
			report_error("Greska: ime " + designatorObj.getName() + " nije nizovskog tipa", designatorExpr);
			designatorExpr.obj = Tab.noObj;
		}
	}

	public boolean checkDesignator(Designator designator) {
		if (designator.obj.getKind() == Obj.Var ||
				designator.obj.getKind() == Obj.Elem ||
				designator.obj.getKind() == Obj.Fld) {
			return true;
		}

		return false;
	}

	public void visit(DesignatorStmtFunCall designatorStmtFunCall) {
		if (designatorStmtFunCall.getDesignator().obj.getKind() != Obj.Meth) {
			report_error("Greska: " + designatorStmtFunCall.getDesignator().obj.getName() + " nije funkcija", designatorStmtFunCall);
		} else {
			formalParamters = designatorStmtFunCall.getDesignator().obj.getLocalSymbols();

			if (designatorStmtFunCall.getDesignator().obj.getLevel() != actualParameters.size()) {
				report_error("Greska: funkciji " + designatorStmtFunCall.getDesignator().obj.getName() + " nije prosledjen odgovarajuci broj argumenata", designatorStmtFunCall);
			} else {
				int i = 0;
				for (Obj currObj: formalParamters) {
					if (i >= actualParameters.size()) {
						break;
					}

					if (!actualParameters.get(i).assignableTo(currObj.getType())) {
						report_error("Greska: stvarni parametar na poziciji " + (i + 1) + " nije odgovarajuceg tipa", designatorStmtFunCall);
					}

					i++;
				}				    		
			}
		}

		formalParamters = null;
		actualParameters.clear();
	}

	public void visit(DesignatorStmtInc designatorStmtInc) {	
		if (!checkDesignator(designatorStmtInc.getDesignator()) || 
				designatorStmtInc.getDesignator().obj.getType() != Tab.intType) {
			report_error("Greska: " + designatorStmtInc.getDesignator().obj.getName() + " nije odgovarajuceg tipa", designatorStmtInc);
		}
	}

	public void visit(DesignatorStmtDec designatorStmtDec) {
		if (!checkDesignator(designatorStmtDec.getDesignator()) || 
				designatorStmtDec.getDesignator().obj.getType() != Tab.intType) {
			report_error("Greska: " + designatorStmtDec.getDesignator().obj.getName() + " nije odgovarajuceg tipa", designatorStmtDec);
		}
	}

	public void visit(DesignatorStmtAssign designatorStmtAssign) {
		if (!checkDesignator(designatorStmtAssign.getDesignator()) || 
				!designatorStmtAssign.getExpr().struct.assignableTo(designatorStmtAssign.getDesignator().obj.getType())) {
			report_error("Greska: tipovi nisu odgovarajuci pri dodeli vrednosti", designatorStmtAssign);
		}
	}

	// factor 
	public void visit(FactorDesignatorNoPars factorDesignatorNoPars) {
		factorDesignatorNoPars.struct = factorDesignatorNoPars.getDesignator().obj.getType();
	}

	public void visit(FactorDesignatorPars factorDesignatorPars) {
		factorDesignatorPars.struct = factorDesignatorPars.getDesignator().obj.getType();

		if (factorDesignatorPars.getDesignator().obj.getKind() != Obj.Meth) {
			report_error("Greska: " + factorDesignatorPars.getDesignator().obj.getName() + " nije funkcija", factorDesignatorPars);
			factorDesignatorPars.struct = Tab.noType;
		} else {
			formalParamters = factorDesignatorPars.getDesignator().obj.getLocalSymbols();

			if (factorDesignatorPars.getDesignator().obj.getLevel() != actualParameters.size()) {
				report_error("Greska: funkciji " + factorDesignatorPars.getDesignator().obj.getName() + " nije prosledjen odgovarajuci broj argumenata", factorDesignatorPars);
			} else {
				/*for (int i = 0; i < actualParameters.size(); i++) {
		    if (!actualParameters.get(i).assignableTo(formalParamters.iterator().next().getType())) {
			report_error("Greska: stvarni parametar na poziciji " + (i + 1) + " nije odgovarajuceg tipa", factorDesignatorPars);
		    }
		}*/

				int i = 0;

				for (Obj currObj: formalParamters) {
					if (i >= actualParameters.size()) {
						break;
					}

					if (!actualParameters.get(i).assignableTo(currObj.getType())) {
						report_error("Greska: stvarni parametar na poziciji " + (i + 1) + " nije odgovarajuceg tipa", factorDesignatorPars);
					}

					i++;
				}
			}
		}

		formalParamters = null;
		actualParameters.clear();
	}

	public void visit(FactorNumber factorNumber) {
		factorNumber.struct = Tab.intType;
	}

	public void visit(FactorChar factorChar) {
		factorChar.struct = Tab.charType;
	}

	public void visit(FactorBool factorBool) {
		factorBool.struct = Tab.find("bool").getType();
	}

	public void visit(FactorNewNoExpr factorNewNoExpr) {
		factorNewNoExpr.struct = factorNewNoExpr.getType().struct;

		if (factorNewNoExpr.getType().struct.getKind() != Struct.Class) {
			report_error("Greska: tip mora biti klasni", factorNewNoExpr);
		}
	}

	public void visit(FactorNewExpr factorNewExpr) {
		// factorNewExpr.struct = factorNewExpr.getType().struct;
		factorNewExpr.struct = new Struct(Struct.Array, factorNewExpr.getType().struct);

		if (factorNewExpr.getExpr().struct != Tab.intType) {
			report_error("Greska: duzina mora biti tipa int", factorNewExpr);
			// factorNewExpr.struct = Tab.noType;
		}
	}

	public void visit(FactorExpr factorExpr) {
		factorExpr.struct = factorExpr.getExpr().struct;
	}       

	// term 
	public void visit(TermFactor termFactor) {
		termFactor.struct = termFactor.getFactor().struct;
	}

	public void visit(TermList termList) {
		termList.struct = Tab.intType;

		if (termList.getTerm().struct != Tab.intType || termList.getFactor().struct != Tab.intType) {
			report_error("Greska: operandi nisu tipa int", termList);
			termList.struct = Tab.noType;
		}		
	}

	// Expr
	public void visit(TermExpr termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}

	public void visit(AddExpr addExpr) {
		addExpr.struct = Tab.intType;

		if (addExpr.getExpression().struct != Tab.intType || addExpr.getTerm().struct != Tab.intType) {
			report_error("Greska: operandi nisu tipa int", addExpr);
			addExpr.struct = Tab.noType;
		}	
	}

	public void visit(Exprss exprss) {
		exprss.struct = exprss.getExpression().struct;
	}

	public void visit(MinusExpr minusExpr) {
		minusExpr.struct = minusExpr.getExpression().struct;

		if (minusExpr.getExpression().struct != Tab.intType) {
			report_error("Greska: izraz mora biti tipa int", minusExpr);
			minusExpr.struct = Tab.noType;
		}
	}

	public void visit(DoWhileStart doWhileStart) {
		doWhileCount++;
	}

	public void visit(DoWhileStmt doWhileStmt) {
		doWhileCount--;

		if (doWhileStmt.getCondition().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz nije tipa bool", doWhileStmt);
		}
	}

	public void visit(BreakStmt breakStmt) {
		if (doWhileCount <= 0) {
			report_error("Greska: break iskaz se moze koristiti samo unutar do-while petlje", breakStmt);
		}
	}

	public void visit(ContinueStmt continueStmt) {
		if (doWhileCount <= 0) {
			report_error("Greska: break iskaz se moze koristiti samo unutar do-while petlje", continueStmt);
		}
	}

	public void visit(ReadStmt readStmt) {		
		if (!checkDesignator(readStmt.getDesignator()) || 
				(readStmt.getDesignator().obj.getType() != Tab.intType &&
				readStmt.getDesignator().obj.getType() != Tab.charType &&
				readStmt.getDesignator().obj.getType() != Tab.find("bool").getType())) {
			report_error("Greska: argument nije ispravan", readStmt);
		}
	}

	public void visit(PrintStmtNoNum printStmtNoNum) {
		if (printStmtNoNum.getExpr().struct != Tab.intType && 
				printStmtNoNum.getExpr().struct != Tab.charType &&
				printStmtNoNum.getExpr().struct != Tab.find("bool").getType()) {
			report_error("Greska: argument nije ispravan", printStmtNoNum);
		}
	}

	public void visit(PrintStmtNum printStmtNum) {
		if (printStmtNum.getExpr().struct != Tab.intType && 
				printStmtNum.getExpr().struct != Tab.charType &&
				printStmtNum.getExpr().struct != Tab.find("bool").getType()) {
			report_error("Greska: argument nije ispravan", printStmtNum);
		}
	}

	public void visit(CondFactExpr condFactExpr) {
		condFactExpr.struct = condFactExpr.getExpr().struct;
	}

	public void visit(CondFactList condFactList) {
		if (!condFactList.getCondFact().struct.compatibleWith(condFactList.getExpr().struct)) {
			report_error("Greska: tipovi nisu kompatabilni", condFactList);
			condFactList.struct = Tab.noType;
		} else if ((condFactList.getCondFact().struct.getKind() == Struct.Class || 
				condFactList.getCondFact().struct.getKind() == Struct.Array) && 
				!(condFactList.getRelop() instanceof Equal || condFactList.getRelop() instanceof NotEqual)) {
			report_error("Greska: klasni i nizovski tipovi se mogu porediti samo po jednakosti", condFactList);
			condFactList.struct = Tab.noType;
		} else {
			condFactList.struct = Tab.find("bool").getType();
		}
	}

	public void visit(CondTermSingle condTermSingle) {
		condTermSingle.struct = condTermSingle.getCondFact().struct;

		/*if (condTermSingle.getCondFact().struct != Tab.find("bool").getType()) {
	    report_error("Greska: izraz nije tipa bool", condTermSingle);
	}*/
	}

	public void visit(CondTermList condTermList) {
		condTermList.struct = condTermList.getCondFact().struct;

		if (condTermList.getCondFact().struct != Tab.find("bool").getType() || 
				condTermList.getCondTerm().struct != Tab.find("bool").getType()) {
			report_error("Greska: izraz nije tipa bool", condTermList);
		}	    
	}

	public void visit(ConditionSingle conditionSingle) {
		conditionSingle.struct = conditionSingle.getCondTerm().struct;
	}

	public void visit(ConditionList conditionList) {
		conditionList.struct = conditionList.getCondition().struct;

		if (conditionList.getCondition().struct != Tab.find("bool").getType() ||
				conditionList.getCondTerm().struct != Tab.find("bool").getType()) {
			report_error("Greska: izraz nije tipa bool", conditionList);
		}	    
	}

	public void visit(IfElseStmt ifElseStmt) {		
		/*if (ifElseStmt.getCondition().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz nije tipa bool", ifElseStmt);
		}*/	
		if (ifElseStmt.getIfStart().getCondition().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz nije tipa bool", ifElseStmt);
		}
	}

	public void visit(UnmatchedIf unmatchedIf) {
		if (unmatchedIf.getIfStart().getCondition().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz nije tipa bool", unmatchedIf);
		}
	}

	public void visit(UnmatchedIfElse unmatchedIfElse) {
		if (unmatchedIfElse.getIfStart().getCondition().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz nije tipa bool", unmatchedIfElse);
		}
	}

	public void visit(ActParExpr actParExpr) {
		actualParameters.add(actParExpr.getExpr().struct);
	}

	public void visit(ActParsList actParsList) {
		actualParameters.add(actParsList.getExpr().struct);
	}

	public boolean passed() {
		return !errorDetected;
	}       
}
