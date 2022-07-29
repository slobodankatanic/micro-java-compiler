// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class FormParameters extends FormPars {

    private FormPars FormPars;
    private SingleFormParameter SingleFormParameter;

    public FormParameters (FormPars FormPars, SingleFormParameter SingleFormParameter) {
        this.FormPars=FormPars;
        if(FormPars!=null) FormPars.setParent(this);
        this.SingleFormParameter=SingleFormParameter;
        if(SingleFormParameter!=null) SingleFormParameter.setParent(this);
    }

    public FormPars getFormPars() {
        return FormPars;
    }

    public void setFormPars(FormPars FormPars) {
        this.FormPars=FormPars;
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
        if(FormPars!=null) FormPars.accept(visitor);
        if(SingleFormParameter!=null) SingleFormParameter.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormPars!=null) FormPars.traverseTopDown(visitor);
        if(SingleFormParameter!=null) SingleFormParameter.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormPars!=null) FormPars.traverseBottomUp(visitor);
        if(SingleFormParameter!=null) SingleFormParameter.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParameters(\n");

        if(FormPars!=null)
            buffer.append(FormPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(SingleFormParameter!=null)
            buffer.append(SingleFormParameter.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParameters]");
        return buffer.toString();
    }
}
