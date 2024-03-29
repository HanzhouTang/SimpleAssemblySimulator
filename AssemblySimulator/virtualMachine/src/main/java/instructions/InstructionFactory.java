package instructions;

import Instructions.Instruction;
import Instructions.Op;
import org.apache.log4j.Logger;
import virtualmachine.ClockCycleCounter;
import virtualmachine.Message;
import virtualmachine.VirtualMachine;

import java.lang.reflect.Constructor;
import java.util.Queue;


public class InstructionFactory {
    private static Logger LOGGER = Logger.getLogger(InstructionFactory.class);

    public static InstructionBase createInstruction(Instruction ins, int readMemoryNeedCycle, int writeMemoryNeedCycle, int executionCycle, VirtualMachine vm) throws Exception {
        Op op = ins.getOpcode();
        String className = "instructions." +
                op.getMemonic().toLowerCase().substring(0, 1).toUpperCase() +
                op.getMemonic().substring(1).toLowerCase() + "Instruction";
        LOGGER.debug("class name " + className);
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(VirtualMachine.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Instruction.class);
        return (InstructionBase) ctor.newInstance(new Object[]{vm, readMemoryNeedCycle, executionCycle, writeMemoryNeedCycle, ins});
    }
}
