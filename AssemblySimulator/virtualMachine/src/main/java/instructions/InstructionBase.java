package instructions;

import Instructions.Instruction;
import Instructions.Mode;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.log4j.Logger;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;
import virtualmachine.VirtualMachine;


import javax.swing.plaf.PanelUI;
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
    //private final Queue<Message> eventRecorder;
    private final VirtualMachine virtualMachine;
    private final int readMemoryNeededCycle;
    private final int writeMemoryNeededCycle;
    private final int executionCycle;
    private final Instruction instruction;
    private Integer startCycle;
    private Integer sourceValue;
    protected static Logger LOGGER = Logger.getLogger(InstructionBase.class);
    private final Mode mode;
    private final InstructionBase lastInstructionStatus;


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

    public Integer getStartCycle() {
        return startCycle;
    }

    public int getWriteMemoryNeededCycle() {
        return writeMemoryNeededCycle;
    }

    /*public Queue<Message> getEventRecorder() {
        return eventRecorder;
    }*/

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setStartCycle(Integer startCycle) {
        this.startCycle = startCycle;
    }

    public void setSourceValue(Integer value) {
        sourceValue = value;
    }

    public InstructionBase getLastInstructionStatus() {
        return lastInstructionStatus;
    }

    protected InstructionBase(VirtualMachine virtualMachine, int rc, int ec, int wc, Instruction ins) {
        this.virtualMachine = virtualMachine;
        readMemoryNeededCycle = rc;
        writeMemoryNeededCycle = wc;
        executionCycle = ec;
        instruction = ins;
        if (instruction.getMemRegister() == null) {
            mode = null;
        } else {
            mode = instruction.getMemRegister().getMode();
        }
        lastInstructionStatus = null;
        //LOGGER.debug("mode " + mode + " for " + instruction);
    }


    public InstructionBase(InstructionBase instructionBase) {
        virtualMachine = instructionBase.virtualMachine;
        readMemoryNeededCycle = instructionBase.getReadMemoryNeededCycle();
        writeMemoryNeededCycle = instructionBase.getWriteMemoryNeededCycle();
        executionCycle = instructionBase.getExecutionCycle();
        instruction = instructionBase.getInstruction();
        mode = instructionBase.getMode();
        lastInstructionStatus = instructionBase;
        startCycle = instructionBase.getStartCycle();
        sourceValue = instructionBase.getSourceValue();
    }

    abstract public Result executeInstruction();

    abstract public InstructionBase copy();

    private Result execute_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            //LOGGER.debug("instruction " + instruction + " is executing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is executing in cycle " + cycle));
        } else if (cycle == finishCycle) {
            //LOGGER.debug("instruction " + instruction + " is executing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is executing in cycle " + cycle));
            return executeInstruction();
        }
        return new Result(null, null, Result.ResultState.EXECUTING);
    }

    private Result read_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            LOGGER.debug("instruction " + instruction + " is reading in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is reading in cycle " + cycle));
        } else if (cycle == finishCycle) {
            LOGGER.debug("instruction " + instruction + " is reading in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is reading in cycle " + cycle));
            return new Result(null, copy(), Result.ResultState.READ_COMPLETE);
        }
        Result result = new Result(null, copy(), Result.ResultState.READING);
        return result;

    }

    private Result write_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle) {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is writing in cycle " + cycle));
        } else if (cycle == finishCycle) {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is writing in cycle " + cycle));
            return new Result(null, copy(), Result.ResultState.WRITE_COMPLETE);
        }
        Result result = new Result(null, copy(), Result.ResultState.WRITING);
        return result;
        // now we need find a way to pass result from reading to writing
    }


    public Result execute(final int cycle) {
        if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.SIB.equals(mode)
                || Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.INDIRECT.equals(mode) || Mode.DISPLACEMENT_ONLY.equals(mode)) {
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
