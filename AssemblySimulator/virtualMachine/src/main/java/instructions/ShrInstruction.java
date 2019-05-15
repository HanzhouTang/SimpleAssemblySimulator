package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class ShrInstruction extends InstructionBase {
    public ShrInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected ShrInstruction(InstructionBase shr) {
        super(shr);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        int result = getSourceValue() >> 1;
        return new Result(result, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new ShrInstruction(this);
    }
}