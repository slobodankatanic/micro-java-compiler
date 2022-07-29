// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class FormalParamsOnlyVar extends FormalParameters {

    private VarArgs VarArgs;

    public FormalParamsOnlyVar (VarArgs VarArgs) {
        this.VarArgs=VarArgs;
        if(VarArgs!=null) VarArgs.setParent(this);
    }

    public VarArgs getVarArgs() {
        return VarArgs;
    }

    public void setVarArgs(VarArgs VarArgs) {
        this.VarArgs=VarArgs;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarArgs!=null) VarArgs.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarArgs!=null) VarArgs.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarArgs!=null) VarArgs.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormalParamsOnlyVar(\n");

        if(VarArgs!=null)
            buffer.append(VarArgs.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormalParamsOnlyVar]");
        return buffer.toString();
    }
}
