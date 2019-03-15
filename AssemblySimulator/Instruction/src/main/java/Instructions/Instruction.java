package Instructions;

public abstract class Instruction {
    final private Op opcode;
    protected Instruction(Op op){
        this.opcode = op;
    }
    protected  Op getOpcode(){
        return opcode;
    }
    public abstract  int toInt();
}
