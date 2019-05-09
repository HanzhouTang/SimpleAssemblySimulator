package instructions;

import Instructions.Instruction;
import Instructions.Mode;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.log4j.Logger;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;


import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

/**
 * The base class of instruction. It implement Runable interface.
 * one instruction should in 3 round
 * 1) read from memory if need
 * 2) do execution
 * 3) write to memory if need
 * To reduce potential inconsistent, the instructionBase will become @immutable
 *
 * @author Hanzhou Tang
 */

@Immutable
public abstract class InstructionBase {
    private final Queue<Message> eventRecorder;
    private final int readMemoryNeededCycle;
    private final int writeMemoryNeededCycle;
    private final int executionCycle;
    private final Instruction instruction;
    private final int startCycle;
    private static Logger LOGGER = Logger.getLogger(InstructionBase.class);
    private final Mode mode;


    public Mode getMode() {
        return mode;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public int getExecutionCycle() {
        return executionCycle;
    }

    public int getReadMemoryNeededCycle() {
        return readMemoryNeededCycle;
    }

    public int getStartCycle() {
        return startCycle;
    }

    public int getWriteMemoryNeededCycle() {
        return writeMemoryNeededCycle;
    }

    public Queue<Message> getEventRecorder() {
        return eventRecorder;
    }


    protected InstructionBase(int startCycle, Queue<Message> q, int rc, int ec, int wc, Instruction ins) {
        eventRecorder = q;
        readMemoryNeededCycle = rc;
        writeMemoryNeededCycle = wc;
        executionCycle = ec;
        instruction = ins;
        this.startCycle = startCycle;
        if (instruction.getMemRegister() == null) {
            mode = null;
        } else {
            mode = instruction.getMemRegister().getMode();
        }
        LOGGER.debug("mode " + mode + " for " + instruction);
    }

    abstract public Result executeInstruction();

    private Result execute_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            LOGGER.debug("instruction " + instruction + " is executing in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is executing in cycle " + cycle));
        } else if (cycle == finishCycle) {
            LOGGER.debug("instruction " + instruction + " is executing in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is executing in cycle " + cycle));
            return null;
        }
        return null;
    }

    private Result read_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            LOGGER.debug("instruction " + instruction + " is reading in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is reading in cycle " + cycle));
        } else if (cycle == finishCycle) {
            LOGGER.debug("instruction " + instruction + " is reading in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is reading in cycle " + cycle));
            return null;
        }
        return null;
    }

    private Result write_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is writing in cycle " + cycle));
        } else if (cycle == finishCycle) {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            eventRecorder.add(new Message("instruction " + instruction + " is writing in cycle " + cycle));
            return null;
        }
        return null;
    }


    public Result execute(final int cycle) {
        if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.SIB.equals(mode)
                || Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.INDIRECT.equals(mode)) {
            if (instruction.isFromMemToReg()) {

                final int readCycle = startCycle + readMemoryNeededCycle;
                final int finishCycle = startCycle + readMemoryNeededCycle + executionCycle;
                if (cycle < readCycle) {
                    return read_(cycle, readCycle);
                } else {
                    return execute_(cycle, finishCycle);
                }
            } else {
                final int finishCycle = startCycle + writeMemoryNeededCycle + executionCycle;
                final int executeCycle = startCycle + executionCycle;
                if (cycle < executeCycle) {
                    return execute_(cycle, executeCycle);
                } else {
                    return write_(cycle, finishCycle);
                }
            }
        } else {
            int finishCycle = startCycle + executionCycle;
            return execute_(cycle, finishCycle);
        }
    }


}
