package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class ShlInstruction extends InstructionBase {
    public ShlInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected ShlInstruction(InstructionBase shl) {
        super(shl);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        return new Result(getSourceValue() << 1, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new ShlInstruction(this);
    }
}