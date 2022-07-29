// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class ConstListSingle extends ConstList {

    private SingleConstant SingleConstant;

    public ConstListSingle (SingleConstant SingleConstant) {
        this.SingleConstant=SingleConstant;
        if(SingleConstant!=null) SingleConstant.setParent(this);
    }

    public SingleConstant getSingleConstant() {
        return SingleConstant;
    }

    public void setSingleConstant(SingleConstant SingleConstant) {
        this.SingleConstant=SingleConstant;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SingleConstant!=null) SingleConstant.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SingleConstant!=null) SingleConstant.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SingleConstant!=null) SingleConstant.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstListSingle(\n");

        if(SingleConstant!=null)
            buffer.append(SingleConstant.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstListSingle]");
        return buffer.toString();
    }
}
