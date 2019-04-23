package Instructions;

/**
 * The instruction class.
 * All source code will convert to consecutive instruction objects.
 */
public abstract class Instruction {
    final private Op opcode;
    final private Register register;
    final private Register reg_mem;
    final private boolean memoryToRegister;
    final private boolean notEightBitsRegister;
    protected Instruction(Op op, Register _register, Register _reg_mem,boolean d, boolean s){
        this.opcode = op;
        register = _register;
        reg_mem = _reg_mem;
        memoryToRegister = d;
        notEightBitsRegister = s;
    }
    protected  Op getOpcode(){
        return opcode;
    }
    protected Register getFrom(){
        if(memoryToRegister){
            return reg_mem;
        }
        else{
            return  register;
        }
    }
    protected Register getTo(){
        if(memoryToRegister){
            return register;
        }
        else{
            return reg_mem;
        }
    }
    protected  Boolean isMemoryToRegister(){
        return memoryToRegister;
    }
    protected Boolean isEightBitsRegister(){
        return !notEightBitsRegister;
    }
    public abstract byte[]  toBytes();
}
