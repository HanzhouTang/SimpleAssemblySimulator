package tomasulo;

import Instructions.Register;
import virtualmachine.RegisterManager;

public class IndirectDependency extends Dependency {
    private final int displacement;
    private final Register register;
    private Integer registerValue = null;
    private Integer address = null;

    @Override
    @SuppressWarnings("Duplicates")
    public Integer getNeededReorderBufferNumber(ReversedTable table, RegisterManager registerManager) {
        if (register != null) {
            //ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, register);
            AddressEntry addressEntry = new AddressEntry(register);
            Integer result = table.getReversedBy(addressEntry);
            if (result != null) {
                return result;
            } else {
                if (registerValue == null) {
                    registerValue = registerManager.getRegister(register).getContent();
                }
                // only read in first time
            }
        }
        if (address == null) {
            address = displacement + registerValue;
        }
        //ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.MEMORY, address, null);
        AddressEntry addressEntry = new AddressEntry(address);
        return table.getReversedBy(addressEntry);

    }

    public Integer getAddress() {
        return address;
    }

    public IndirectDependency(int displacement, Register register) {
        this.displacement = displacement;
        this.register = register;
    }
}
