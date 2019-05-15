package tomasulo;

import Instructions.Register;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ReservedTable {
    private static Logger LOGGER = Logger.getLogger(ReservedTable.class);
    final private Map<Integer, Integer> memoryReversedTable = new HashMap<>();
    final private Map<Register, Integer> registerMapReversedTable = new HashMap<>();

    void add(AddressEntry entry, Integer orderBufferNumber) {
        if (AddressEntry.Type.MEMORY.equals(entry.getType())) {
            memoryReversedTable.put(entry.getMemoryAddress(), orderBufferNumber);
        } else if (AddressEntry.Type.REGISTER.equals(entry.getType())) {
            registerMapReversedTable.put(entry.getRegister(), orderBufferNumber);
        }
    }

    Integer getReversedBy(AddressEntry entry) {
        if (AddressEntry.Type.MEMORY.equals(entry.getType())) {
            return memoryReversedTable.get(entry.getMemoryAddress());
        } else if (AddressEntry.Type.REGISTER.equals(entry.getType())) {
            return registerMapReversedTable.get(entry.getRegister());
        }
        return null;
    }

    void remove(AddressEntry entry) {
        if (AddressEntry.Type.MEMORY.equals(entry.getType())) {
            memoryReversedTable.remove(entry.getMemoryAddress());
        } else if (AddressEntry.Type.REGISTER.equals(entry.getType())) {
            registerMapReversedTable.remove(entry.getRegister());
        }
    }

    public Set<Map.Entry<Integer, Integer>> memoryToSet() {
        return memoryReversedTable.entrySet();
    }

    public Set<Map.Entry<Register, Integer>> registerToSet() {
        return registerMapReversedTable.entrySet();
    }
}
