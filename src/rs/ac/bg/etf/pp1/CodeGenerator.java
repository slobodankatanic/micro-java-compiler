package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;

	private ArrayList<Integer> ifFixup = new ArrayList<>();
	private ArrayList<Integer> elseFixup = new ArrayList<>();
	private ArrayList<Integer> doWhileAdresses = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> continueFixup = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> breakFixup = new ArrayList<>();	
	
	private int doWhileCnt = 0;
	
	private ArrayList<Obj> methodCalls = new ArrayList<>();
	private ArrayList<Integer> methodCallActParsNum = new ArrayList<>();
	
	private Logger log = Logger.getLogger(getClass());
	
	public int getMainPc() {
		return mainPc;
	}

	public void visit(PrintStmtNum printStmtNum) {
		if (printStmtNum.getExpr().struct == Tab.intType
				|| printStmtNum.getExpr().struct == Tab.find("bool").getType()) {
			Code.loadConst(printStmtNum.getN2());
			Code.put(Code.print);
		} else {
			Code.loadConst(printStmtNum.getN2());
			Code.put(Code.bprint);
		}
	}

	public void visit(PrintStmtNoNum printStmtNoNum) {
		if (printStmtNoNum.getExpr().struct == Tab.intType
				|| printStmtNoNum.getExpr().struct == Tab.find("bool").getType()) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
	}

	public void visit(MinusExpr minusExpr) {
		Code.put(Code.neg);
	}
	
	public void visit(AddExpr addExpr) {
		Addop addop = addExpr.getAddop();
		
		if (addop instanceof Add) {
			Code.put(Code.add);
		} else {	
			Code.put(Code.sub);
		}
	}
	
	public void visit(TermList termList) {
		Mulop mulop = termList.getMulop();
		
		if (mulop instanceof Mul) {
			Code.put(Code.mul);
		} else if (mulop instanceof Div) {
			Code.put(Code.div);
		} else {
			Code.put(Code.rem);
		}
	}
	
	// factor
	public void visit(FactorNumber factorNumber) {
		Obj constObj = Tab.insert(Obj.Con, "$", Tab.intType);
		constObj.setLevel(0);
		constObj.setAdr(factorNumber.getN1());
		Code.load(constObj);
	}

	public void visit(FactorChar factorChar) {
		Obj constObj = Tab.insert(Obj.Con, "$", Tab.charType);
		constObj.setLevel(0);
		constObj.setAdr(factorChar.getC1());
		Code.load(constObj);
	}

	public void visit(FactorBool factorBool) {
		Obj constObj = Tab.insert(Obj.Con, "$", Tab.find("bool").getType());
		constObj.setLevel(0);
		constObj.setAdr(factorBool.getB1() ? 1 : 0);
		Code.load(constObj);
	}

	public void visit(FactorNewNoExpr factorNewNoExpr) {
		int size = factorNewNoExpr.struct.getMembers().size() * 4;

		Code.put(Code.new_);
		Code.put2(size);
	}

	public void visit(FactorNewExpr factorNewExpr) {
		Code.put(Code.newarray);

		if (factorNewExpr.struct == Tab.charType) {
			Code.put(0);
		} else {
			Code.put(1);
		}
	}

	// do-while
	public void visit(DoWhileStart doWhileStart) {
		doWhileCnt++;
		doWhileAdresses.add(Code.pc);
		continueFixup.add(new ArrayList<Integer>());
		breakFixup.add(new ArrayList<Integer>());
	}
	
	public void visit(CondTermList condTermList) {
		Code.put(Code.mul);
	}
	
	public void visit(ConditionList conditionList) {
		Code.put(Code.add);
	}
	
	public void visit(CondFactList condFactList) {
		int opCode = 0;
		
		if (condFactList.getRelop() instanceof Equal) {
			opCode = Code.ne;						
		} else if (condFactList.getRelop() instanceof NotEqual) {
			opCode = Code.eq;
		} else if (condFactList.getRelop() instanceof Greater) {
			opCode = Code.le;
		} else if (condFactList.getRelop() instanceof GreaterEqual) {
			opCode = Code.lt;
		} else if (condFactList.getRelop() instanceof Less) {
			opCode = Code.ge;
		} else {
			opCode = Code.gt;
		}
		
		Code.putFalseJump(opCode, Code.pc + 7);
		Code.loadConst(0);
		Code.putJump(Code.pc + 4);
		Code.loadConst(1);
	}
	
	public void visit(IfStart ifStart) {
		Code.put(Code.const_1);
		ifFixup.add(Code.pc + 1);
		Code.putFalseJump(Code.ge, 0);
	}
	
	public void visit(Else els) {
		elseFixup.add(Code.pc + 1);
		Code.putJump(0);
		
		int addr = ifFixup.get(ifFixup.size() - 1);
		Code.fixup(addr);
		ifFixup.remove(ifFixup.size() - 1);
	}
	
	public void visit(UnmatchedIf unmatchedIf) {
		int addr = ifFixup.get(ifFixup.size() - 1);
		Code.fixup(addr);
		ifFixup.remove(ifFixup.size() - 1);
	}
	
	public void visit(IfElseStmt ifElseStmt) {
		int addr = elseFixup.get(elseFixup.size() - 1);
		Code.fixup(addr);
		elseFixup.remove(elseFixup.size() - 1);
	}
	
	public void visit(UnmatchedIfElse unmatchedIfElse) {
		int addr = elseFixup.get(elseFixup.size() - 1);
		Code.fixup(addr);
		elseFixup.remove(elseFixup.size() - 1);
	}
	
	public void visit(DoWhileStmt doWhileStmt) {
		Code.put(Code.const_1);
		Code.putFalseJump(Code.lt, doWhileAdresses.get(doWhileAdresses.size() - 1));
		
		for (int adr : breakFixup.get(breakFixup.size() - 1)) {
			Code.fixup(adr);
		}
		
		doWhileAdresses.remove(doWhileAdresses.size() - 1);
		continueFixup.remove(continueFixup.size() - 1);
		breakFixup.remove(breakFixup.size() - 1);
		doWhileCnt--;
	}
	
	public void visit(DoWhileEnd doWhileEnd) {
		for (int adr : continueFixup.get(continueFixup.size() - 1)) {
			Code.fixup(adr);
		}						
	}
	
	public void visit(ContinueStmt continueStmt) {
		// contfixup
		continueFixup.get(continueFixup.size() - 1).add(Code.pc + 1);				
		Code.putJump(0);		
	}
	
	public void visit(BreakStmt breakStmt) {
		/*if (breakFixup.size() < doWhileCnt) {
			breakFixup.add(new ArrayList<Integer>());
		}*/
		
		breakFixup.get(breakFixup.size() - 1).add(Code.pc + 1);
		
		Code.putJump(0);
	}
	
	public void visit(ReadStmt readStmt) {
		Code.put(Code.read);
		Code.store(readStmt.getDesignator().obj);
	}
	
	public void visit(MethodTypeName methodTypeName) {
		if (methodTypeName.getMethName().equals("main")) {
			mainPc = Code.pc;
		}

		methodTypeName.obj.setAdr(Code.pc);

		Code.put(Code.enter);
		Code.put(methodTypeName.obj.getLevel());
		Code.put(methodTypeName.obj.getLocalSymbols().size());
	}

	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(DesignatorIdent designatorIdent) {
		if (!(designatorIdent.getParent() instanceof DesignatorStmtAssign
				|| designatorIdent.getParent() instanceof ReadStmt 
				|| designatorIdent.getParent() instanceof FactorDesignatorPars 
				|| designatorIdent.getParent() instanceof DesignatorStmtFunCall)) {
			Code.load(designatorIdent.obj);
		}
		
		// MODIFICATION: July
		if (designatorIdent.obj.getKind() == Obj.Meth) {
			log.info(designatorIdent.getParent().toString());			
			methodCalls.add(designatorIdent.obj);			
			methodCallActParsNum.add(0);
		}
	}
	
	public void visit(DesignatorExpr designatorExpr) {
		if (!(designatorExpr.getParent() instanceof DesignatorStmtAssign
				|| designatorExpr.getParent() instanceof ReadStmt)) {
			if (designatorExpr.getParent() instanceof DesignatorStmtInc
					|| designatorExpr.getParent() instanceof DesignatorStmtDec) {
				Code.put(Code.dup2);
			}
			
			Code.load(designatorExpr.obj);
		}
	}

	public void visit(DesignatorDot designatorDot) {
		if (!(designatorDot.getParent() instanceof DesignatorStmtAssign
				|| designatorDot.getParent() instanceof ReadStmt)) {
			if (designatorDot.getParent() instanceof DesignatorStmtInc
					|| designatorDot.getParent() instanceof DesignatorStmtDec) {
				Code.put(Code.dup);
			}
			
			Code.load(designatorDot.obj);			
		}
	}

	public void visit(DesignatorStmtAssign designatorStmtAssign) {
		Code.store(designatorStmtAssign.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtFunCall designatorStmtFunCall) {
		Obj functionObj = designatorStmtFunCall.getDesignator().obj;
		// int offset = functionObj.getAdr() - Code.pc;
		
		if (functionObj.getName().equals("len")) {
			Code.put(Code.arraylength);
			return;
		}
		
		if (functionObj.getName().equals("chr") || functionObj.getName().equals("ord")) {
			return;
		}
		
		// MODIFICATION: July
		if (designatorStmtFunCall.getDesignator().obj == methodCalls.get(methodCalls.size() - 1)) {
			log.info("Equal");
		} else {
			log.info("Not equal");
		}
		
		Collection<Obj> formalParameters = designatorStmtFunCall.getDesignator().obj.getLocalSymbols();
		
		int formalParametersCount = 0;
		boolean argVar = false;
		Obj argVarObj = null;
		
		for (Obj currParam : formalParameters) {
			if (currParam.getFpPos() > 0) {
				formalParametersCount++;
				if (currParam.getFpPos() == 2) {
					argVar = true;
					argVarObj = currParam; 
				}
			}
		}
		
		if (argVar) {
			int varArgSize = methodCallActParsNum
					.get(methodCallActParsNum.size() - 1) - formalParametersCount + 1;
			
			Code.loadConst(varArgSize);
			
			Code.put(Code.newarray);

			if (argVarObj.getType().getElemType() == Tab.charType) {
				Code.put(0);
			} else {
				Code.put(1);
			}						
			
			// now we have parameters and array address for variable argument on expression stack
			
			for (int i = 0; i < varArgSize; i++) {
				// 1, 2, 3, a
				Code.put(Code.dup_x1);
				Code.put(Code.dup_x1);
				
				// 1, 2, a, a, 3, a				
				Code.put(Code.pop);
				
				// 1, 2, a, a, 3
				Code.loadConst(varArgSize - i - 1);
				
				// 1, 2, a, a, 3, i
				Code.put(Code.dup_x1);
				
				// 1, 2, a, a, i, 3, i
				Code.put(Code.pop);
				
				// 1, 2, a, (a, i, 3)
				if (argVarObj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			}
		}
		
		int offset = functionObj.getAdr() - Code.pc;
		// MODIFICATION: July				
		
		Code.put(Code.call);
		Code.put2(offset);
		
		if (functionObj.getType() != Tab.noType) {
			Code.put(Code.pop);
		}
		
		// MODIFICATION: July
		methodCalls.remove(methodCalls.size() - 1);
		methodCallActParsNum.remove(methodCallActParsNum.size() - 1);
	}
	
	public void visit(FactorDesignatorPars factorDesignatorPars) {
		Obj functionObj = factorDesignatorPars.getDesignator().obj;
		// int offset = functionObj.getAdr() - Code.pc;		
		
		if (functionObj.getName().equals("len")) {
			Code.put(Code.arraylength);
			return;
		}
		
		if (functionObj.getName().equals("chr") || functionObj.getName().equals("ord")) {
			return;
		}
		
		// MODIFICATION: July
		if (factorDesignatorPars.getDesignator().obj == methodCalls.get(methodCalls.size() - 1)) {
			log.info("Equal");
		} else {
			log.info("Not equal");
		}
		
		Collection<Obj> formalParameters = factorDesignatorPars.getDesignator().obj.getLocalSymbols();
		
		int formalParametersCount = 0;
		boolean argVar = false;
		Obj argVarObj = null;
		
		for (Obj currParam : formalParameters) {
			if (currParam.getFpPos() > 0) {
				formalParametersCount++;
				if (currParam.getFpPos() == 2) {
					argVar = true;
					argVarObj = currParam; 
				}
			}
		}
		
		if (argVar) {
			int varArgSize = methodCallActParsNum
					.get(methodCallActParsNum.size() - 1) - formalParametersCount + 1;
			
			Code.loadConst(varArgSize);
			
			Code.put(Code.newarray);

			if (argVarObj.getType().getElemType() == Tab.charType) {
				Code.put(0);
			} else {
				Code.put(1);
			}						
			
			// now we have parameters and array address for variable argument on expression stack
			
			for (int i = 0; i < varArgSize; i++) {
				// 1, 2, 3, a
				Code.put(Code.dup_x1);	
				Code.put(Code.dup_x1);
				
				// 1, 2, a, a, 3, a				
				Code.put(Code.pop);
				
				// 1, 2, a, a, 3
				Code.loadConst(varArgSize - i - 1);
				
				// 1, 2, a, a, 3, i
				Code.put(Code.dup_x1);
				
				// 1, 2, a, a, i, 3, i
				Code.put(Code.pop);
				
				// 1, 2, a, (a, i, 3)
				if (argVarObj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			}
		}
		
		int offset = functionObj.getAdr() - Code.pc;
		// MODIFICATION: July
		
		Code.put(Code.call);
		Code.put2(offset);
		
		// MODIFICATION: July
		methodCalls.remove(methodCalls.size() - 1);
		methodCallActParsNum.remove(methodCallActParsNum.size() - 1);
	}
	
	public void visit(DesignatorStmtInc designatorStmtInc) {
		Code.put(Code.const_1);
		Code.put(Code.add);
		Code.store(designatorStmtInc.getDesignator().obj);
	}

	public void visit(DesignatorStmtDec designatorStmtDec) {
		Code.put(Code.const_1);
		Code.put(Code.sub);
		Code.store(designatorStmtDec.getDesignator().obj);
	}
	
	// MODIFICATION: July
	public void visit(ActParExpr actParExpr) {	
		methodCallActParsNum.set(methodCallActParsNum.size() - 1, 
				methodCallActParsNum.get(methodCallActParsNum.size() - 1) + 1);
	}

	public void visit(ActParsList actParsList) {		
		methodCallActParsNum.set(methodCallActParsNum.size() - 1, 
				methodCallActParsNum.get(methodCallActParsNum.size() - 1) + 1);
	}

}
