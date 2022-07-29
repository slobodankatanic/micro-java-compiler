// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class ProgramPartsConst extends ProgramPartsList {

    private ProgramPartsList ProgramPartsList;
    private ConstDecl ConstDecl;

    public ProgramPartsConst (ProgramPartsList ProgramPartsList, ConstDecl ConstDecl) {
        this.ProgramPartsList=ProgramPartsList;
        if(ProgramPartsList!=null) ProgramPartsList.setParent(this);
        this.ConstDecl=ConstDecl;
        if(ConstDecl!=null) ConstDecl.setParent(this);
    }

    public ProgramPartsList getProgramPartsList() {
        return ProgramPartsList;
    }

    public void setProgramPartsList(ProgramPartsList ProgramPartsList) {
        this.ProgramPartsList=ProgramPartsList;
    }

    public ConstDecl getConstDecl() {
        return ConstDecl;
    }

    public void setConstDecl(ConstDecl ConstDecl) {
        this.ConstDecl=ConstDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.accept(visitor);
        if(ConstDecl!=null) ConstDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramPartsList!=null) ProgramPartsList.traverseTopDown(visitor);
        if(ConstDecl!=null) ConstDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.traverseBottomUp(visitor);
        if(ConstDecl!=null) ConstDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgramPartsConst(\n");

        if(ProgramPartsList!=null)
            buffer.append(ProgramPartsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDecl!=null)
            buffer.append(ConstDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgramPartsConst]");
        return buffer.toString();
    }
}
