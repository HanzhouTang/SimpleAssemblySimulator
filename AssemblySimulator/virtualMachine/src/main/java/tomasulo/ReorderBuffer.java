package tomasulo;

import Instructions.Mode;
import Instructions.Operand;
import Instructions.Register;
import config.VirtualMachineProperties;
import instructions.InstructionBase;
import instructions.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import virtualmachine.VirtualMachine;

@Component
public class ReorderBuffer {
    private static Logger LOGGER = Logger.getLogger(ReorderBuffer.class);

    enum ReorderBufferState {EXEC, WAIT_EXEC, ISSUE, COMMIT}


    public static class ReorderBufferEntry {
        final InstructionBase executedInstruction;
        boolean isBusy = false;
        final Result result;
        final ReorderBufferState state;

        public ReorderBufferEntry(InstructionBase ins, boolean b, Result r, ReorderBufferState s) {
            executedInstruction = ins;
            isBusy = b;
            result = r;
            state = s;
        }
    }

    private final ReorderBufferEntry buffer[];
    private int size = 0;
    private int head = 0;

    @Autowired
    public ReorderBuffer(VirtualMachineProperties properties) {
        buffer = new ReorderBufferEntry[properties.getReorderBufferSize()];
    }

    @SuppressWarnings("Duplicates")
    boolean add(InstructionBase instructionBase, VirtualMachine vm) throws Exception {

        // need update reservation table here
        if (isFull()) {
            return false;
        }
        ReversedTable reversedTable = vm.getReversedTable();
        Mode mode = instructionBase.getMode();
        if (instructionBase.getInstruction().isFromMemToReg()) {
            Register register = instructionBase.getInstruction().getRegister().getRegister();
            ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, register);
            reversedTable.add(entry, size);
        } else {
            Mode memRegMode = instructionBase.getInstruction().getMemRegister().getMode();
            if (Mode.REGISTER.equals(memRegMode)) {
                Register register = instructionBase.getInstruction().getMemRegister().getRegister();
                ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, register);
                reversedTable.add(entry, size);
            } else {
                Dependency dependency = DependencyFactory.createDependency(instructionBase.getInstruction().getMemRegister());
                if (dependency.getNeededReorderBufferNumber(reversedTable, vm.getRegisterManager()) == null) {
                    Integer memory = dependency.getAddress();
                    ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.MEMORY, memory, null);
                    reversedTable.add(entry, size);
                } else {
                    vm.sendMessage("The destination address of instruction "
                            + instructionBase.getInstruction() +
                            " is depended on others, cannot issue the instruction");
                    return false;
                }
            }
        }
        int location = (head + size) % buffer.length;
        ReorderBufferEntry entry = new ReorderBufferEntry(instructionBase, true, null, ReorderBufferState.ISSUE);
        buffer[location] = entry;
        size++;
        return true;
    }

    public void removeLastestEntry() {
        if (size > 0) {
            size--;
        }
    }

    public boolean isFull() {
        return buffer.length == size;
    }

}
