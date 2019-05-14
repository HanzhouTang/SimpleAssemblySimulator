package instructions;

import Instructions.Instruction;
import Instructions.Mode;
import Instructions.Operand;
import Instructions.Register;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.apache.log4j.Logger;
import tomasulo.Dependency;
import tomasulo.DependencyFactory;
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
    protected final VirtualMachine virtualMachine;
    private final int readMemoryNeededCycle;
    private final int writeMemoryNeededCycle;
    private final int executionCycle;
    private final Instruction instruction;
    private Integer startCycle;
    private Integer sourceValue;
    protected Integer result = null;
    protected static Logger LOGGER = Logger.getLogger(InstructionBase.class);
    private final Mode mode;



    public Mode getMode() {
        return mode;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(int r) {
        result = r;
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

    protected int getDestinationValue() throws Exception {
        Operand destination = instruction.getDestination();
        Mode dest_mode = destination.getMode();
        if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(dest_mode) || Mode.SIB.equals(dest_mode)
                || Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(dest_mode) || Mode.INDIRECT.equals(dest_mode) || Mode.DISPLACEMENT_ONLY.equals(dest_mode)) {
            Dependency dependency = DependencyFactory.createDependency(destination);
            dependency.getNeededReorderBufferNumber(virtualMachine.getReversedTable(), virtualMachine.getRegisterManager());
            int mem = dependency.getAddress();
            return virtualMachine.getMemory().get32(mem);
        } else {
            Register r = destination.getRegister();
            return virtualMachine.getRegisterManager().getRegister(r).getContent();
        }
    }

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setStartCycle(Integer startCycle) {
        this.startCycle = startCycle;
    }

    public void setSourceValue(Integer value) {
        sourceValue = value;
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

        //LOGGER.debug("mode " + mode + " for " + instruction);
    }


    public InstructionBase(InstructionBase instructionBase) {
        virtualMachine = instructionBase.virtualMachine;
        readMemoryNeededCycle = instructionBase.getReadMemoryNeededCycle();
        writeMemoryNeededCycle = instructionBase.getWriteMemoryNeededCycle();
        executionCycle = instructionBase.getExecutionCycle();
        instruction = instructionBase.getInstruction();
        mode = instructionBase.getMode();
        startCycle = instructionBase.getStartCycle();
        sourceValue = instructionBase.getSourceValue();
    }

    abstract public Result executeInstruction() throws Exception;

    abstract public InstructionBase copy();

    public InstructionBase copyWithResult(int result) {
        InstructionBase ret = copy();
        ret.setResult(result);
        return ret;
    }

    private Result execute_(final int cycle, final int finishCycle) throws Exception {
        if (cycle < finishCycle - 1) {
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is executing in cycle " + cycle));
        } else {
            virtualMachine.sendMessage(new Message("instruction " + instruction + " finish executing in cycle " + cycle));
            return executeInstruction();
        }
        return new Result(null, copy(), Result.ResultState.EXECUTING);
    }

    private Result read_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle - 1) {
            virtualMachine.sendMessage(new Message("instruction " + instruction + " finish reading in cycle " + cycle));
        } else {
            virtualMachine.sendMessage(new Message("instruction " + instruction + " finish reading in cycle " + cycle));
            return new Result(null, copy(), Result.ResultState.READ_COMPLETE);
        }
        Result result = new Result(null, copy(), Result.ResultState.READING);
        return result;

    }

    private Result write_(final int cycle, final int finishCycle) {
        if (cycle < finishCycle - 1) {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " is writing in cycle " + cycle));
        } else {
            LOGGER.debug("instruction " + instruction + " is writing in cycle " + cycle);
            virtualMachine.sendMessage(new Message("instruction " + instruction + " finish writing in cycle " + cycle));
            return new Result(getResult(), copy(), Result.ResultState.WRITE_COMPLETE);
        }
        Result result = new Result(getResult(), copy(), Result.ResultState.WRITING);
        return result;
        // now we need find a way to pass result from reading to writing
    }


    @SuppressWarnings("Duplicates")
    public Result execute(final int cycle) throws Exception {
        if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.SIB.equals(mode)
                || Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.INDIRECT.equals(mode) || Mode.DISPLACEMENT_ONLY.equals(mode)) {
            if (instruction.isFromMemToReg()) {

                final int readCycle = startCycle + readMemoryNeededCycle;
                final int finishCycle = startCycle + readMemoryNeededCycle + executionCycle;
                //LOGGER.info("current cycle " + cycle + " readCycle " + readCycle + " finishCycle " + finishCycle);
                if (cycle < readCycle) {
                    return read_(cycle, readCycle);
                } else {

                    Result ret = execute_(cycle, finishCycle);
                    if (Result.ResultState.EXEC_COMPLETE.equals(ret.getState())) {
                        return new Result(ret.getResult(), ret.getInstructionBase(), Result.ResultState.COMPLETE);
                    }
                    return ret;
                }
            } else {
                final int finishCycle = startCycle + writeMemoryNeededCycle + executionCycle;
                final int executeCycle = startCycle + executionCycle;
                if (cycle < executeCycle) {
                    return execute_(cycle, executeCycle);
                } else {
                    Result ret = write_(cycle, finishCycle);
                    if (Result.ResultState.WRITE_COMPLETE.equals(ret.getState())) {
                        return new Result(ret.getResult(), ret.getInstructionBase(), Result.ResultState.COMPLETE);
                    }
                    return ret;
                }
            }
        } else {
            int finishCycle = startCycle + executionCycle;
            Result ret = execute_(cycle, finishCycle);
            if (Result.ResultState.EXEC_COMPLETE.equals(ret.getState())) {
                return new Result(ret.getResult(), ret.getInstructionBase(), Result.ResultState.COMPLETE);
            }
            return ret;
        }
    }


}
