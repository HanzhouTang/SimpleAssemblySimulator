package tomasulo;

import Instructions.Instruction;
import Instructions.Mode;
import Instructions.Operand;
import Instructions.Register;
import config.VirtualMachineProperties;
import instructions.InstructionBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import virtualmachine.VirtualMachine;

@Component
public class ReservationStation {

    public class ReservationStationEntry {
        boolean isBusy;
        final InstructionBase instruction;
        Integer vj;

        final Integer qj;


        public InstructionBase getInstruction() {
            return instruction;
        }

        public boolean isBusy() {
            return isBusy;
        }


        public Integer getVj() {
            return vj;
        }


        public void setBusy(boolean busy) {
            isBusy = busy;
        }


        public void setVj(Integer vj) {
            this.vj = vj;
        }

        public Integer getQj() {
            return qj;
        }


        public ReservationStationEntry(final InstructionBase ins, Integer orderBufferNumber) {
            instruction = ins;
            qj = orderBufferNumber;
        }
    }
    // only affect read , write is ok.

    private final ReservationStationEntry[] table;
    int head = 0;
    int size = 0;

    @Autowired
    public ReservationStation(VirtualMachineProperties properties) {
        table = new ReservationStationEntry[properties.getReservationStationSize()];
    }

    @SuppressWarnings("Duplicates")
    public boolean issueInstruction(InstructionBase instructionBase, VirtualMachine vm) throws Exception {
        if (isFull()) {
            return false;
        }
        ReorderBuffer reorderBuffer = vm.getReorderBuffer();
        ReversedTable reversedTable = vm.getReversedTable();
        if (!reorderBuffer.add(instructionBase, vm)) {
            return false;
        }
        int location = (head + size) % table.length;
        Instruction instruction = instructionBase.getInstruction();
        ReservationStationEntry reservationStationEntry = null;
        if (instructionBase.getInstruction().isFromMemToReg()) {
            Mode mode = instructionBase.getInstruction().getMemRegister().getMode();
            if (Mode.REGISTER.equals(mode)) {
                Register register = instructionBase.getInstruction().getMemRegister().getRegister();
                ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, register);
                Integer number = reversedTable.getReversedBy(entry);
                reservationStationEntry = new ReservationStationEntry(instructionBase, number);
                if (number == null) {
                    Integer value = vm.getRegisterManager().getRegister(instructionBase.getInstruction().getMemRegister().getRegister()).getContent();
                    reservationStationEntry.setVj(value);
                }
            } else {
                Dependency dependency = DependencyFactory.createDependency(instructionBase.getInstruction().getMemRegister());
                if (dependency.getNeededReorderBufferNumber(reversedTable, vm.getRegisterManager()) == null) {
                    Integer memory = dependency.getAddress();
                    ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.MEMORY, memory, null);
                    Integer number = reversedTable.getReversedBy(entry);
                    reservationStationEntry = new ReservationStationEntry(instructionBase, number);
                    if (number == null) {
                        Integer value = vm.getRegisterManager().getRegister(instructionBase.getInstruction().getMemRegister().getRegister()).getContent();
                        reservationStationEntry.setVj(value);
                    }
                } else {
                    vm.sendMessage("The source address of instruction "
                            + instructionBase.getInstruction() +
                            " is depended on others, cannot issue the instruction");
                    reorderBuffer.removeLastestEntry();// remove from reorder buffer
                    return false;
                }
            }
        } else {
            Operand source = instructionBase.getInstruction().getRegister();
            if (Mode.IMMEDIATE.equals(source.getMode())) {
                reservationStationEntry = new ReservationStationEntry(instructionBase, null);
                reservationStationEntry.setVj(source.getImmediate());
            } else {
                Register register = source.getRegister();
                ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, register);
                Integer number = reversedTable.getReversedBy(entry);
                reservationStationEntry = new ReservationStationEntry(instructionBase, number);
                if (number == null) {
                    Integer value = vm.getRegisterManager().getRegister(instructionBase.getInstruction().getMemRegister().getRegister()).getContent();
                    reservationStationEntry.setVj(value);
                }
            }
        }
        table[location] = reservationStationEntry;
        size++;
        vm.sendMessage("Instruction " + instructionBase.getInstruction() + " is issued");
        return true;
    }

    public boolean isFull() {
        return table.length == size;
    }
}
