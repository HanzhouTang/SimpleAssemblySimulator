package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class SubInstruction extends InstructionBase {
    public SubInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected SubInstruction(InstructionBase sub) {
        super(sub);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        int destinationValue = getDestinationValue();
        int result = destinationValue - getSourceValue();
        return new Result(result, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new SubInstruction(this);
    }
}