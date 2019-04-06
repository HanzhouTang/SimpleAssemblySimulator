package OutputFile;

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
}
