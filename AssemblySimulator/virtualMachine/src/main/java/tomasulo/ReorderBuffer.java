package tomasulo;

import Instructions.Mode;
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

    enum ReorderBufferState {EXEC, WAIT_EXEC, ISSUE, COMMIT, WAIT_COMMIT}

    public static class ReorderBufferEntry {
        final InstructionBase executedInstruction;
        boolean isBusy = false;
        final Result result;
        final ReorderBufferState state;
        final int reservationIndex;
        final AddressEntry dest;

        public ReorderBufferEntry(InstructionBase ins, boolean b, Result r, ReorderBufferState s, int reservationIndex, final AddressEntry dest) {
            executedInstruction = ins;
            isBusy = b;
            result = r;
            state = s;
            this.reservationIndex = reservationIndex;
            this.dest = dest;
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
    boolean add(InstructionBase instructionBase, VirtualMachine vm, int reservationIndex) throws Exception {

        // need update reservation table here
        if (isFull()) {
            return false;
        }
        AddressEntry dest = null;
        ReversedTable reversedTable = vm.getReversedTable();
        Mode mode = instructionBase.getMode();
        if (instructionBase.getInstruction().isFromMemToReg()) {
            Register register = instructionBase.getInstruction().getRegister().getRegister();
            dest = new AddressEntry(register);
            reversedTable.add(dest, size);
        } else {
            Mode memRegMode = null;
            if (instructionBase.getInstruction().getMemRegister() != null) {
                memRegMode = instructionBase.getInstruction().getMemRegister().getMode();
            }
            if (Mode.REGISTER.equals(memRegMode)) {
                Register register = instructionBase.getInstruction().getMemRegister().getRegister();
                dest = new AddressEntry(register);
                reversedTable.add(dest, size);
            } else {
                Dependency dependency = DependencyFactory.createDependency(instructionBase.getInstruction().getMemRegister());
                Integer dependedReorderBufferIndex = null;
                if (dependency != null) {
                    dependedReorderBufferIndex = dependency.getNeededReorderBufferNumber(reversedTable, vm.getRegisterManager());
                    if (dependedReorderBufferIndex == null) {
                        Integer memory = dependency.getAddress();
                        dest = new AddressEntry(memory);
                        reversedTable.add(dest, size);
                    } else {
                        vm.sendMessage("The destination address of instruction "
                                + instructionBase.getInstruction() +
                                " is depended on reorder buffer #" + dependedReorderBufferIndex + ", cannot issue the instruction");
                        return false;
                    }
                }
            }
        }
        int location = (head + size) % buffer.length;
        ReorderBufferEntry entry = new ReorderBufferEntry(instructionBase, true, null, ReorderBufferState.ISSUE, reservationIndex, dest);
        buffer[location] = entry;
        size++;
        return true;
    }


    /**
     * I should check if finished by status
     *
     * @param vm
     * @throws Exception
     */
    @SuppressWarnings("Duplicates")
    public void run(VirtualMachine vm) throws Exception {
        ReservationStation reservationStation = vm.getReservationStation();
        int loc = head % buffer.length;
        if (size > 0 && ReorderBufferState.WAIT_COMMIT.equals(buffer[loc].state)) {
            ReorderBufferEntry entry = buffer[loc];
            ReorderBufferEntry new_entry = new ReorderBufferEntry(null, false, entry.result, ReorderBufferState.COMMIT, entry.reservationIndex, entry.dest);
            vm.sendMessage("Instruction " + entry.executedInstruction.getInstruction() + " is committed");
            //LOGGER.info("entry destination " + entry.dest);
            buffer[loc] = new_entry;
            if (entry.result != null && entry.result.getResult() != null) {
                if (AddressEntry.Type.REGISTER.equals(entry.dest.getType())) {
                    vm.getRegisterManager().setRegisterValue(entry.dest.getRegister(), entry.result.getResult());
                } else {
                    vm.getMemory().set(entry.dest.getMemoryAddress(), entry.result.getResult());
                }
            }
            if (entry.dest != null) {
                ReversedTable reversedTable = vm.getReversedTable();
                reversedTable.remove(entry.dest);
            }
            ReservationStation.ReservationStationEntry oldEntry = reservationStation.get(entry.reservationIndex);
            ReservationStation.ReservationStationEntry newEntry = new ReservationStation.ReservationStationEntry(entry.executedInstruction, null);
            newEntry.setVj(entry.result.getResult());
            reservationStation.set(entry.reservationIndex, newEntry);
            removeFirstEntry();
        }
        for (int i = 0; i < size; i++) {
            int index = (head + i) % buffer.length;
            ReorderBufferEntry entry = buffer[index];
            if (entry.isBusy) {
                ReorderBufferEntry new_entry = null;
                if (ReorderBufferState.WAIT_EXEC.equals(entry.state) || ReorderBufferState.ISSUE.equals(entry.state)) {
                    ReservationStation.ReservationStationEntry reservationStationEntry = reservationStation.get(entry.reservationIndex);
                    // For now, if qj equals to null, start execution.
                    if (reservationStationEntry.getQj() == null) {
                        entry.executedInstruction.setSourceValue(reservationStationEntry.getVj());
                        // reservationStationEntry become useless
                        reservationStation.removeEntry(entry.reservationIndex);
                        entry.executedInstruction.setStartCycle(vm.getClockCycleCounter().getCurrentClockCycle());
                        Result r = entry.executedInstruction.execute(vm.getClockCycleCounter().getCurrentClockCycle());
                        InstructionBase next = r == null ? null : r.getInstructionBase();
                        if (r == null || Result.ResultState.COMPLETE.equals(r.getState())) {
                            vm.sendMessage("Instruction " + entry.executedInstruction.getInstruction() + " is wait for commit");
                            new_entry = new ReorderBufferEntry(entry.executedInstruction, false, r, ReorderBufferState.WAIT_COMMIT, entry.reservationIndex, entry.dest);
                        } else {
                            new_entry = new ReorderBufferEntry(next, true, r, ReorderBufferState.EXEC, entry.reservationIndex, entry.dest);
                        }
                    } else {
                        vm.sendMessage("Instruction " + entry.executedInstruction.getInstruction() + " is wait for buffer entry #" + reservationStationEntry.getQj());
                        new_entry = new ReorderBufferEntry(entry.executedInstruction, true, null, ReorderBufferState.WAIT_EXEC, entry.reservationIndex, entry.dest);
                    }
                } else {
                    Result r = entry.executedInstruction.execute(vm.getClockCycleCounter().getCurrentClockCycle());
                    InstructionBase next = r == null ? null : r.getInstructionBase();
                    if (r == null || Result.ResultState.COMPLETE.equals(r.getState())) {
                        new_entry = new ReorderBufferEntry(entry.executedInstruction, false, r, ReorderBufferState.WAIT_COMMIT, entry.reservationIndex, entry.dest);
                        vm.sendMessage("Instruction " + entry.executedInstruction.getInstruction() + " is wait for commit");
                    } else {
                        new_entry = new ReorderBufferEntry(next, true, r, ReorderBufferState.EXEC, entry.reservationIndex, entry.dest);
                    }
                }

                buffer[index] = new_entry;
            } else {
                // push result to common data bus
                if (ReorderBufferState.WAIT_COMMIT.equals(entry.state)) {
                    ReservationStation.ReservationStationEntry reservationStationEntry = reservationStation.get(entry.reservationIndex);
                    reservationStationEntry.setVj(entry.result.getResult());
                }
            }
        }
    }

    void removeLatestEntry() {
        if (size > 0) {
            size--;
        }
    }

    void removeFirstEntry() {
        if (size > 0) {
            head++;
            size--;
        }
    }

    public boolean isFull() {
        return buffer.length == size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
