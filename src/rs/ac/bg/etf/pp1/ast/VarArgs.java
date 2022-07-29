// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class VarArgs implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Type Type;
    private String varArgsName;

    public VarArgs (Type Type, String varArgsName) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.varArgsName=varArgsName;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getVarArgsName() {
        return varArgsName;
    }

    public void setVarArgsName(String varArgsName) {
        this.varArgsName=varArgsName;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarArgs(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+varArgsName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarArgs]");
        return buffer.toString();
    }
}
