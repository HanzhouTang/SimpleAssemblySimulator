package instructions;

import Instructions.Instruction;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;
import virtualmachine.VirtualMachine;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class NopInstruction extends InstructionBase {

    public NopInstruction(VirtualMachine vm, int rc, int ec, int wc, Instruction ins) {
        super(vm, rc, ec, wc, ins);
    }

    protected NopInstruction(InstructionBase nop) {
        super(nop);
    }

    @Override
    final public Result executeInstruction() {
        return new Result(null, null, Result.ResultState.EXEC_COMPLETE);
    }

    @Override
    final public InstructionBase copy() {
        return new NopInstruction(this);
    }
}
