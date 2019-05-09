package instructions;

import Instructions.Instruction;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class NopInstruction extends InstructionBase {

    public NopInstruction(int counter, Queue<Message> q, int rc, int ec, int wc, Instruction ins) {
        super(counter, q, rc, ec, wc, ins);
    }

    @Override
    final public Result executeInstruction() {
        return null;
    }
}
