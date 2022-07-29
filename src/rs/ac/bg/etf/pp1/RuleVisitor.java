package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;

public class RuleVisitor extends VisitorAdaptor{

	int printCallCount = 0;
	int varDeclCount = 0;
 	
	Logger log = Logger.getLogger(getClass());

	public void visit(VarDecl vardecl){
		varDeclCount++;
	}
	
    public void visit(PrintStmtNum print) {
		printCallCount++;
		// log.info("Prepoznata print naredba");
	}	
	
    public void visit(PrintStmtNoNum print) {
		printCallCount++;
		// log.info("Prepoznata print naredba");
	}
    
}