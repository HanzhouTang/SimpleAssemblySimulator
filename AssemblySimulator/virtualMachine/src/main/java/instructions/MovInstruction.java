package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class MovInstruction extends InstructionBase {

    public MovInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected MovInstruction(InstructionBase mov) {
        super(mov);
    }

    @Override
    final public Result executeInstruction() {
        LOGGER.info("source value " + getSourceValue());
        return new Result(null, null, Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new MovInstruction(this);
    }
}
