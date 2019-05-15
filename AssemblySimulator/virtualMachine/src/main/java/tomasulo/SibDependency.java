package tomasulo;

import Instructions.Register;
import virtualmachine.RegisterManager;

public class SibDependency extends Dependency {

    final Register index;
    final Register base;
    final int scale;
    final int displacement;
    Integer indexValue = null;
    Integer baseValue = null;
    Integer address = null;

    @Override
    @SuppressWarnings("Duplicates")
    public Integer getNeededReorderBufferNumber(ReservedTable table, RegisterManager registerManager) {
        if (index != null) {
            AddressEntry addressEntry = new AddressEntry(index);
            //ReversedTable.ReversedEntry entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, index);
            Integer result = table.getReversedBy(addressEntry);
            if (result != null) {
                return result;
            } else {
                if (indexValue == null) {
                    indexValue = registerManager.getRegister(index).getContent();
                }
            }
        }
        if (base != null) {
            AddressEntry addressEntry = new AddressEntry(base);// entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.REGISTER, null, base);
            Integer result = table.getReversedBy(addressEntry);
            if (result != null) {
                return result;
            } else {
                if (baseValue == null) {
                    baseValue = registerManager.getRegister(base).getContent();
                }
            }
        }
        if (address == null) {
            address = baseValue + indexValue * scale + displacement;
        }
        AddressEntry addressEntry = new AddressEntry(address);//entry = new ReversedTable.ReversedEntry(ReversedTable.KeyType.MEMORY, address, null);
        return table.getReversedBy(addressEntry);

    }

    public SibDependency(Register index, Register base, int scale, int displacement) {
        this.index = index;
        this.base = base;
        this.scale = scale;
        this.displacement = displacement;
    }

    public Integer getAddress() {
        return address;
    }

}
