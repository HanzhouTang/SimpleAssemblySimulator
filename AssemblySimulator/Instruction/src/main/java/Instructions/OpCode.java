package Instructions;

import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OpCode implements Op {
    MOV("000000"),
    PUSH("000001"),
    POP("000010"),
    ADD("000011"),
    SUB("000100"),
    CBW("000101"),
    CWD("000110"),
    BSWAP("000111"),
    AND("001000"),
    OR("001001"),
    XOR("001010"),
    NOT("001011"),
    SAL("001100"),
    PHL("001101"),
    SAR("001111"),
    SHR("010000"),
    CMPSB("010001"),
    CMPSW("010010"),
    CMPSD("010011"),
    LODSB("010100"),
    LODSW("010101"),
    LODSD("010110"),
    STOSB("010111"),
    STOSW("011000"),
    STOSD("011001"),
    REP("011010"),
    REPNE("011011"),
    JMP("011100"),
    JCC("011101"),
    JECXZ("011110"),
    CALL("011111"),
    RET("100000"),
    ENTER("100001"),
    LEAVE("100010"),
    LOOP("100011"),
    LOOPE("100100"),
    LOOPZ("100101"),
    LOOPNE("100110"),
    LOOPNZ("100111"),
    NOP("101000");
    private final BitSet bitSet;

    OpCode( String bits) {
        bitSet = BitSetUtils.fromString(bits);
    }

    @Override
    public String getMemonic() {
        return name();
    }

    @Override
    public BitSet getBits() {
        return (BitSet) bitSet.clone();
    }

    public static Optional<Op> of(final String bits) {
        return Optional.ofNullable(bitsToOpCode.get(bits));
    }

    private static final Map<String, Op> stringToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    private static final Map<String, Op> bitsToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(op -> BitSetUtils.toString(op.getBits(), 6), Function.identity()));

    public static Optional<Op> fromString(final String mem) {
        return Optional.ofNullable(stringToOpCode.get(mem.toUpperCase()));
    }
}
