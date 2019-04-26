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
    CMPSB("010100"), // the opcode of CMPSB, CMPSW and CMPSD are same. Because the length of register will distinguish them.
    CMPSW("010100"),
    CMPSD("010100"),
    LODSB("010101"), // the opcode of LODSB, LODSW and LODSD are same. Because the length of register will distinguish them.
    LODSW("010101"),
    LODSD("010101"),
    STOSB("010110"), // the opcode of STOSB, STOSW and STOSD are same. Because the length of register will distinguish them.
    STOSW("010110"),
    STOSD("010110"),
    REP("010111"),
    REPNE("011000"),
    JMP("011001"),
    JCC("011010"),
    JECXZ("011011"),
    CALL("011100"),
    RET("011101"),
    ENTER("011110"),
    LEAVE("011111"),
    LOOP("100000"),
    LOOPE("100001"),
    LOOPZ("100010"),
    LOOPNE("100011"),
    LOOPNZ("100100"),
    NOP("100101");
    private final String opcode;

    OpCode(String bits) {
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
        try {
            bitSet = BitSetUtils.fromString(opcode);
        } catch (Exception e) {
            getOpLogger().info("catch exception " + e);
        }
        return bitSet;
    }

    @Override
    public String getOpCode() {
        return opcode;
    }

    public static Optional<Op> of(final String opcode) {
        return Optional.ofNullable(bitsToOpCode.get(opcode));
    }

    public static Optional<Op> of(final String opcode, final RegisterLength length) {
        if (RegisterLength.SIXTEEN.equals(length)) {
            if ("010100".equals(opcode)) {
                return Optional.of(CMPSW);
            } else if ("010101".equals(opcode)) {
                return Optional.of(LODSW);
            } else if ("010110".equals(opcode)) {
                return Optional.of(STOSW);
            }
        } else if (RegisterLength.THIRY_TWO.equals(length)) {
            if ("010100".equals(opcode)) {
                return Optional.of(CMPSD);
            } else if ("010101".equals(opcode)) {
                return Optional.of(LODSD);
            } else if ("010110".equals(opcode)) {
                return Optional.of(STOSD);
            }
        } else {
            if ("010100".equals(opcode)) {
                return Optional.of(CMPSB);
            } else if ("010101".equals(opcode)) {
                return Optional.of(LODSB);
            } else if ("010110".equals(opcode)) {
                return Optional.of(STOSB);
            }
        }
        return Optional.ofNullable(bitsToOpCode.get(opcode));
    }

    private static final Map<String, Op> memToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    private static final Map<String, Op> bitsToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Op::getOpCode, Function.identity(),(op1,op2)->op2));

    public static Optional<Op> fromMem(final String mem) {
        return Optional.ofNullable(memToOpCode.get(mem.toUpperCase()));
    }
}
