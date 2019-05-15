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
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        return new Result(getSourceValue(), copyWithResult(getSourceValue()), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new MovInstruction(this);
    }
}
