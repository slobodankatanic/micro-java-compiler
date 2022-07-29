// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class FormParameter extends FormPars {

    private SingleFormParameter SingleFormParameter;

    public FormParameter (SingleFormParameter SingleFormParameter) {
        this.SingleFormParameter=SingleFormParameter;
        if(SingleFormParameter!=null) SingleFormParameter.setParent(this);
    }

    public SingleFormParameter getSingleFormParameter() {
        return SingleFormParameter;
    }

    public void setSingleFormParameter(SingleFormParameter SingleFormParameter) {
        this.SingleFormParameter=SingleFormParameter;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SingleFormParameter!=null) SingleFormParameter.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SingleFormParameter!=null) SingleFormParameter.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SingleFormParameter!=null) SingleFormParameter.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParameter(\n");

        if(SingleFormParameter!=null)
            buffer.append(SingleFormParameter.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParameter]");
        return buffer.toString();
    }
}
