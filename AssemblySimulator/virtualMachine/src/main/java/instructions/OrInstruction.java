package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class OrInstruction extends InstructionBase {
    public OrInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected OrInstruction(InstructionBase or) {
        super(or);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        int destinationValue = getDestinationValue();
        int result = destinationValue | getSourceValue();
        return new Result(result, copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new OrInstruction(this);
    }
}