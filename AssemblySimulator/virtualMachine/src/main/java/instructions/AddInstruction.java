package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class AddInstruction extends InstructionBase {
    public AddInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected AddInstruction(InstructionBase add) {
        super(add);
    }

    @Override
    final public Result executeInstruction() {
        LOGGER.info("source value " + getSourceValue());
        return new Result(null, copy(), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new AddInstruction(this);
    }
}
