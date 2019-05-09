package virtualmachine;

import Instructions.Instruction;
import config.VirtualMachineProperties;
import instructions.InstructionBase;
import instructions.InstructionFactory;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import tomasulo.ReorderBuffer;
import tomasulo.ReservationStation;
import tomasulo.ReversedTable;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * The virtual machine class.
 * There are two way to mimic several instruction run at the same time.
 * 1) the method I tried now, is to use thread.
 * 2) contain a queue which contain Set&lt;Function&rt; which is action taken in every clock cycle.
 * I am not sure which one is better for now.
 * It seems like method 1 is not very good.
 *
 * @author Hanzhou Tang
 */

@Component
public class VirtualMachine {
    @Autowired
    RegisterManager registerManager;

    @Autowired
    ClockCycleCounter clockCycleCounter;

    @Autowired
    Memory memory;

    @Autowired
    VirtualMachineProperties setting;

    @Autowired
    ReservationStation reservationStation;

    @Autowired
    ReorderBuffer reorderBuffer;

    @Autowired
    ReversedTable reversedTable;

    int finalPoint = 0;

    Queue<Message> eventRecorder = new LinkedList<>();
    private static Logger LOGGER = Logger.getLogger(VirtualMachine.class);

    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    public ReorderBuffer getReorderBuffer() {
        return reorderBuffer;
    }

    public ReversedTable getReversedTable() {
        return reversedTable;
    }

    public Queue<Message> getEventRecorder() {
        return eventRecorder;
    }

    public void loadObjFile(String name) throws Exception {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(name))) {
            int checker = dataInputStream.readInt();
            if (checker != 0x7f) {
                throw new Exception("not a valid obj file");
            }
            int baseAddress = dataInputStream.readInt();
            int entryPoint = dataInputStream.readInt();
            finalPoint = dataInputStream.readInt();
            List<Byte> data = new ArrayList<>();
            byte[] bytes = new byte[4096];
            int len;
            while ((len = dataInputStream.read(bytes)) > 0) {
                Byte[] tmp = new Byte[len];
                for (int i = 0; i < len; i++) {
                    tmp[i] = bytes[i];
                }
                data.addAll(Arrays.asList(tmp));
            }
            memory.setData(data);
            PhysicalRegister pc = registerManager.getRegister("pc");
            pc.setContent(entryPoint);// error: must set register by manger
            LOGGER.info("total read size:     \t" + memory.size());
            LOGGER.info("entry point address: \t" + pc.getContent());
            LOGGER.info("end point address:   \t" + finalPoint);
        } catch (Exception e) {
            throw e;
        }
    }

    public void sendMessage(String msg) {
        eventRecorder.add(new Message(msg + " in cycle " + clockCycleCounter.getCurrentClockCycle()));
        LOGGER.debug(msg + " in cycle " + clockCycleCounter.getCurrentClockCycle());
    }

    public void run() throws Exception {
        Instruction nextInstruction = getNextInstruction();
        while (nextInstruction != null) {
            if (issueInstruction(nextInstruction)) {
                nextInstruction = getNextInstruction();
            }
            executeInstructions();
            clockCycleCounter.toNextClockCycle();

        }
    }

    private void executeInstructions() {

    }

    private boolean issueInstruction(Instruction instruction) throws Exception {
        if (instruction == null) {
            throw new Exception("cannot issue a null instruction");
        } else {
            final int loadCycle = setting.getLoadMemoryNeededCycle();
            final int executionCycle = setting.getExecutionTime(instruction.getOpcode());
            final int saveCycle = setting.getSaveMemoryNeededCycle();
            InstructionBase instructionBase = InstructionFactory.createInstruction(instruction, loadCycle, saveCycle, executionCycle, eventRecorder);
            return reservationStation.issueInstruction(instructionBase, this);
        }

    }

    public Instruction getNextInstruction() throws Exception {
        PhysicalRegister pc = registerManager.getRegister("pc");
        int location;
        if ((location = pc.getContent()) < finalPoint) {
            MutableInt loc = new MutableInt(location);
            Instruction instruction = Instruction.fromBytes(memory.getData(), loc);
            pc.setContent(loc.getValue());// error: must set register by manger
            eventRecorder.add(new Message("set pc register points to location " + pc.getContent()));
            LOGGER.debug("set pc register points to location " + pc.getContent());
            return instruction;
        }
        return null;
    }

}
