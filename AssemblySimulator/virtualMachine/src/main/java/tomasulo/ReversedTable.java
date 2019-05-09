package tomasulo;

import Instructions.Register;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReversedTable {
    public enum KeyType {
        MEMORY, REGISTER
    }

    final private Map<Integer, Integer> memoryReversedTable = new HashMap<>();
    final private Map<Register, Integer> registerMapReversedTable = new HashMap<>();

    public static class ReversedEntry {
        final KeyType type;
        final Integer memoryAddress;
        final Register register;

        public ReversedEntry(KeyType k, Integer i, Register r) {
            type = k;
            memoryAddress = i;
            register = r;
        }
    }

    void add(ReversedEntry entry, Integer orderBufferNumber) {
        if (KeyType.MEMORY.equals(entry.type)) {
            memoryReversedTable.put(entry.memoryAddress, orderBufferNumber);
        } else if (KeyType.REGISTER.equals(entry.type)) {
            registerMapReversedTable.put(entry.register, orderBufferNumber);
        }
    }

    Integer getReversedBy(ReversedEntry entry) {
        if (KeyType.MEMORY.equals(entry.type)) {
            return memoryReversedTable.get(entry.memoryAddress);
        } else if (KeyType.REGISTER.equals(entry.type)) {
            return registerMapReversedTable.get(entry.register);
        }
        return null;
    }
}
