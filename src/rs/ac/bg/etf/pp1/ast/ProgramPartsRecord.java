// generated with ast extension for cup
// version 0.8
// 28/5/2022 12:46:28


package rs.ac.bg.etf.pp1.ast;

public class ProgramPartsRecord extends ProgramPartsList {

    private ProgramPartsList ProgramPartsList;
    private RecordDecl RecordDecl;

    public ProgramPartsRecord (ProgramPartsList ProgramPartsList, RecordDecl RecordDecl) {
        this.ProgramPartsList=ProgramPartsList;
        if(ProgramPartsList!=null) ProgramPartsList.setParent(this);
        this.RecordDecl=RecordDecl;
        if(RecordDecl!=null) RecordDecl.setParent(this);
    }

    public ProgramPartsList getProgramPartsList() {
        return ProgramPartsList;
    }

    public void setProgramPartsList(ProgramPartsList ProgramPartsList) {
        this.ProgramPartsList=ProgramPartsList;
    }

    public RecordDecl getRecordDecl() {
        return RecordDecl;
    }

    public void setRecordDecl(RecordDecl RecordDecl) {
        this.RecordDecl=RecordDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.accept(visitor);
        if(RecordDecl!=null) RecordDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ProgramPartsList!=null) ProgramPartsList.traverseTopDown(visitor);
        if(RecordDecl!=null) RecordDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ProgramPartsList!=null) ProgramPartsList.traverseBottomUp(visitor);
        if(RecordDecl!=null) RecordDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgramPartsRecord(\n");

        if(ProgramPartsList!=null)
            buffer.append(ProgramPartsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(RecordDecl!=null)
            buffer.append(RecordDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgramPartsRecord]");
        return buffer.toString();
    }
}
