package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class MulInstruction extends InstructionBase {
    public MulInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected MulInstruction(InstructionBase mul) {
        super(mul);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        int destinationValue = getDestinationValue();
        int result = destinationValue * getSourceValue();
        return new Result(result, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new MulInstruction(this);
    }
}