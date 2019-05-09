package instructions;

import Instructions.Instruction;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class NopInstruction extends InstructionBase {

    public NopInstruction(Queue<Message> q, int rc, int ec, int wc, Instruction ins) {
        super(q, rc, ec, wc, ins);
    }

    protected NopInstruction(InstructionBase nop) {
        super(nop);
    }

    @Override
    final public Result executeInstruction() {
        return null;
    }

    @Override
    final public InstructionBase copy() {
        return new NopInstruction(this);
    }
}
