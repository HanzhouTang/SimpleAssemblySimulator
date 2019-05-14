package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class XorInstruction extends InstructionBase {
    public XorInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected XorInstruction(InstructionBase xor) {
        super(xor);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        int destinationValue = getDestinationValue();
        int result = destinationValue ^ getSourceValue();
        return new Result(result, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new XorInstruction(this);
    }
}