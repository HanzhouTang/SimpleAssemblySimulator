package OutputFile;

import java.util.Optional;

/**
 * For now, our virtual support 4 data types.
 * <ul>
 *     <li>byte(8 bits)</li>
 *     <li>word(16 bits)</li>
 *     <li>dword(32 bits)</li>
 *     <li>qword(512 bits)</li>
 * </ul>
 */
// the smallest unit here is byte, 8 bits = 1 byte
public enum DataType {
    BYTE(1),
    WORD(2),
    DWORD(4),
    QWORD(64);
    final private int size;
    DataType(int s) {
        size = s;
    }
    public static Optional<DataType> of(String name){
        DataType dataType = null;
        try{
            dataType = DataType.valueOf(name.toUpperCase());
        }
        catch (Exception e){
            dataType = null;
        }
        return Optional.ofNullable(dataType);
    }
    public int getSize(){
        return size;
    }
}
