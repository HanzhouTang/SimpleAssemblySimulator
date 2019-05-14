package instructions;

import Instructions.Instruction;
import virtualmachine.VirtualMachine;

public class NotInstruction extends InstructionBase {
    public NotInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected NotInstruction(InstructionBase not) {
        super(not);
    }

    @Override
    final public Result executeInstruction() throws Exception {
        LOGGER.debug("source value " + getSourceValue() + " for instruction " + getInstruction());
        return new Result(~getSourceValue(), copyWithResult(result), Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new OrInstruction(this);
    }
}