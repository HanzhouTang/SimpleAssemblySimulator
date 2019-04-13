package Instructions;

import common.BitSetUtils;

import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * All Enum types inherit from Op interface.
 */
public enum OpCode implements Op {
    ADD("000000"),
    ADC("000001"),
    SUB("000010"),
    SBB("000011"),
    MUL("000100"),
    DIV("000101"),
    MOV("000110"),
    PUSH("000111"),
    POP("001000"),
    CBW("001001"),
    CWD("001010"),
    BSWAP("001011"),
    AND("001100"),
    OR("001101"),
    XOR("001110"),
    NOT("001111"),
    SAL("010000"),
    SHL("010001"),
    SAR("010010"),
    SHR("010011"),
    CMPSB("010100"),
    CMPSW("010101"),
    CMPSD("010110"),
    LODSB("010111"),
    LODSW("011000"),
    LODSD("011001"),
    STOSB("011010"),
    STOSW("011011"),
    STOSD("011100"),
    REP("011101"),
    REPNE("011110"),
    JMP("011111"),
    JCC("100000"),
    JECXZ("100001"),
    CALL("100010"),
    RET("100011"),
    ENTER("100100"),
    LEAVE("100101"),
    LOOP("100110"),
    LOOPE("100111"),
    LOOPZ("101000"),
    LOOPNE("101001"),
    LOOPNZ("101010"),
    NOP("101011");
    private final String opcode;

    OpCode( String bits) {
        assert bits.length() == Op.SIZE;
        opcode = bits;
    }

    @Override
    public String getMemonic() {
        return name();
    }

    @Override
    public BitSet getBits() {
        BitSet bitSet = null;
        try{
            bitSet = BitSetUtils.fromString(opcode);
        }
        catch (Exception e){
            getOpLogger().info("catch exception " + e);
        }
        return bitSet;
    }

    @Override
    public  String getOpCode(){
        return opcode;
    }

    public static Optional<Op> of(final String opcode) {
        return Optional.ofNullable(bitsToOpCode.get(opcode));
    }

    private static final Map<String, Op> memToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    private static final Map<String, Op> bitsToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Op::getOpCode, Function.identity()));

    public static Optional<Op> fromMem(final String mem) {
        return Optional.ofNullable(memToOpCode.get(mem.toUpperCase()));
    }
}
