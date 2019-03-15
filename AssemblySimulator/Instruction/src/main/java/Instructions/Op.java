package Instructions;

import java.util.BitSet;
import java.util.Optional;

public interface Op {
    public String getMemonic();
    public BitSet getOpCode();
    public default String getOpCodeStr() {
        return BitSetUtils.toString(getOpCode(),6);
    }
}
