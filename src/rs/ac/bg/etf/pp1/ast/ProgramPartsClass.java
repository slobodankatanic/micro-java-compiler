// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class ProgramPartsClass extends ProgramPartsList {

    private ProgramPartsList ProgramPartsList;
    private ClassDecl ClassDecl;

    public ProgramPartsClass (ProgramPartsList ProgramPartsList, ClassDecl ClassDecl) {
        this.ProgramPartsList=ProgramPartsList;
        if(ProgramPartsList!=null) ProgramPartsList.setParent(this);
        this.ClassDecl=ClassDecl;
        if(ClassDecl!=null) ClassDecl.setParent(this);
    }

    public ProgramPartsList getProgramPartsList() {
        return ProgramPartsList;
    }

    public void setProgramPartsList(ProgramPartsList ProgramPartsList) {
        this.ProgramPartsList=ProgramPartsList;
    }

    public ClassDecl getClassDecl() {
        return ClassDecl;
    }

    public void setClassDecl(ClassDecl ClassDecl) {
        this.ClassDecl=ClassDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.accept(visitor);
        if(ClassDecl!=null) ClassDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramPartsList!=null) ProgramPartsList.traverseTopDown(visitor);
        if(ClassDecl!=null) ClassDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.traverseBottomUp(visitor);
        if(ClassDecl!=null) ClassDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgramPartsClass(\n");

        if(ProgramPartsList!=null)
            buffer.append(ProgramPartsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassDecl!=null)
            buffer.append(ClassDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgramPartsClass]");
        return buffer.toString();
    }
}
