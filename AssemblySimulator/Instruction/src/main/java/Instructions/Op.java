package Instructions;

import java.util.BitSet;

public interface Op {
    public String getMemonic();
    public BitSet getBits();
    public default String getOpCodeStr() {
        return BitSetUtils.toString(getBits(),6);
    }
    public default int toInt(){
        int tmp = (int)getBits().toLongArray()[0];
        return tmp << 24;
    }
}
