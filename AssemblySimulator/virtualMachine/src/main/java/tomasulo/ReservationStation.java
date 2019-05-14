package tomasulo;

import Instructions.Instruction;
import Instructions.Mode;
import Instructions.Operand;
import Instructions.Register;
import config.VirtualMachineProperties;
import instructions.InstructionBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import render.Render;
import virtualmachine.RegisterManager;
import virtualmachine.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationStation {

    public static class ReservationStationEntry {
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
            // orderBufferNumber is the index of re-order buffer for source.
            instruction = ins;
            qj = orderBufferNumber;
        }
    }
    // only affect read , write is ok.

    public List<Render.ReservationStationEntryWrapper> toList() {
        List<Render.ReservationStationEntryWrapper> ret = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Render.ReservationStationEntryWrapper wrapper = new Render.ReservationStationEntryWrapper(table[i], i);
            ret.add(wrapper);
        }
        return ret;
    }

    private final ReservationStationEntry[] table;
    //int head = 0;
    int size = 0;

    @Autowired
    public ReservationStation(VirtualMachineProperties properties) {
        table = new ReservationStationEntry[properties.getReservationStationSize()];
    }

    ReservationStationEntry get(int index) {
        return table[index];
    }

    void set(int index, ReservationStationEntry entry) {
        table[index] = entry;
    }


    private ReservationStationEntry getReservationStationEntryByRegister(Register register, InstructionBase instructionBase,
                                                                         ReservedTable reservedTable, RegisterManager registerManager) {

        AddressEntry addressEntry = new AddressEntry(register);
        Integer number = reservedTable.getReversedBy(addressEntry);
        ReservationStationEntry reservationStationEntry = new ReservationStationEntry(instructionBase, number);
        if (number == null) {
            Integer value = registerManager.getRegister(register).getContent();
            reservationStationEntry.setVj(value);
        }
        return reservationStationEntry;
    }

    public boolean issueInstruction(InstructionBase instructionBase, VirtualMachine vm) throws Exception {
        if (isFull()) {
            return false;
        }
        ReorderBuffer reorderBuffer = vm.getReorderBuffer();
        ReservedTable reservedTable = vm.getReservedTable();
        int location = -1;
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                location = i;
                break;

            }
        }

        if (!reorderBuffer.add(instructionBase, vm, location)) {
            return false;
        }
        Instruction instruction = instructionBase.getInstruction();
        ReservationStationEntry reservationStationEntry = null;
        if (instructionBase.getInstruction().isFromMemToReg()) {
            Mode mode = instructionBase.getInstruction().getMemRegister().getMode();
            if (Mode.REGISTER.equals(mode)) {
                Register register = instructionBase.getInstruction().getMemRegister().getRegister();
                reservationStationEntry = getReservationStationEntryByRegister(register, instructionBase,
                        reservedTable, vm.getRegisterManager());

            } else {
                if (instructionBase.getInstruction().getMemRegister() == null) {
                    reservationStationEntry = new ReservationStationEntry(instructionBase, null);
                } else {
                    Dependency dependency = DependencyFactory.createDependency(instructionBase.getInstruction().getMemRegister());
                    Integer dependedReorderBufferIndex = null;
                    if (dependency != null) {
                        dependedReorderBufferIndex = dependency.getNeededReorderBufferNumber(reservedTable, vm.getRegisterManager());
                        if (dependedReorderBufferIndex == null) {
                            // the source address can be known, for example, if source is [eax] the eax is not reversed by others.
                            Integer memoryAddress = dependency.getAddress();
                            AddressEntry addressEntry = new AddressEntry(memoryAddress);
                            Integer number = reservedTable.getReversedBy(addressEntry);
                            // if some instruction will write to the address, for example, if source is [eax] eax = 1, check if some instruction is writing to [1]
                            reservationStationEntry = new ReservationStationEntry(instructionBase, number);
                            if (number == null) {

                                /* read from memory */
                                /* we pretend that the virtual machine has read or write phase. However, in fact, it doesn't. */
                                /* for now, we assume every operand is 32 bits*/
                                Integer value = vm.getMemory().get32(memoryAddress);
                                //memoryAddress;

                                reservationStationEntry.setVj(value);
                            }
                        } else {
                            vm.sendMessage("The source address of instruction "
                                    + instructionBase.getInstruction() +
                                    " is depended on " + dependedReorderBufferIndex + ", cannot issue the instruction");
                            reorderBuffer.removeLatestEntry();// remove from reorder buffer
                            return false;
                        }
                    }

                }


            }
        } else {
            Operand source = instructionBase.getInstruction().getRegister();
            if (source == null) {
                reservationStationEntry = new ReservationStationEntry(instructionBase, null);
            } else {
                if (Mode.IMMEDIATE.equals(source.getMode())) {
                    reservationStationEntry = new ReservationStationEntry(instructionBase, null);
                    reservationStationEntry.setVj(source.getImmediate());
                } else {
                    Register register = source.getRegister();
                    reservationStationEntry = getReservationStationEntryByRegister(register, instructionBase,
                            reservedTable, vm.getRegisterManager());
                }
            }
        }
        table[location] = reservationStationEntry;
        size++;
        vm.sendMessage("Instruction " + instructionBase.getInstruction() + " is issued");
        return true;
    }

    boolean isFull() {
        return table.length == size;
    }

    void removeEntry(int location) {
        table[location] = null;
        size--;
    }

}
