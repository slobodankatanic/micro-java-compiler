// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class ProgramPartsVar extends ProgramPartsList {

    private ProgramPartsList ProgramPartsList;
    private VarDecl VarDecl;

    public ProgramPartsVar (ProgramPartsList ProgramPartsList, VarDecl VarDecl) {
        this.ProgramPartsList=ProgramPartsList;
        if(ProgramPartsList!=null) ProgramPartsList.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
    }

    public ProgramPartsList getProgramPartsList() {
        return ProgramPartsList;
    }

    public void setProgramPartsList(ProgramPartsList ProgramPartsList) {
        this.ProgramPartsList=ProgramPartsList;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramPartsList!=null) ProgramPartsList.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgramPartsVar(\n");

        if(ProgramPartsList!=null)
            buffer.append(ProgramPartsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgramPartsVar]");
        return buffer.toString();
    }
}
