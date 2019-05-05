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
    AND("001010"),
    OR("001011"),
    XOR("001100"),
    NOT("001101"),
    SHL("001110"),
    SHR("001111"),
    CMPSB("010000"), // the opcode of CMPSB, CMPSW and CMPSD are same. Because the length of register will distinguish them.
    CMPSW("010000"),
    CMPSD("010000"),
    LODSB("010001"), // the opcode of LODSB, LODSW and LODSD are same. Because the length of register will distinguish them.
    LODSW("010001"),
    LODSD("010001"),
    STOSB("010010"), // the opcode of STOSB, STOSW and STOSD are same. Because the length of register will distinguish them.
    STOSW("010010"),
    STOSD("010010"),
    REP("010011"),
    REPNE("010100"),
    JMP("010101"),
    JCC("010110"),
    JECXZ("010111"),
    CALL("011000"),
    RET("011001"),
    ENTER("011010"),
    LEAVE("011011"),
    LOOP("011100"),
    LOOPZ("011101"),
    LOOPNZ("011110"),
    NOP("011111");
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
            if (CMPSW.getOpCode().equals(opcode)) {
                return Optional.of(CMPSW);
            } else if (LODSW.getOpCode().equals(opcode)) {
                return Optional.of(LODSW);
            } else if (STOSW.getOpCode().equals(opcode)) {
                return Optional.of(STOSW);
            }
        } else if (RegisterLength.THIRY_TWO.equals(length)) {
            if (CMPSD.getOpCode().equals(opcode)) {
                return Optional.of(CMPSD);
            } else if (LODSD.getOpCode().equals(opcode)) {
                return Optional.of(LODSD);
            } else if (STOSD.getOpCode().equals(opcode)) {
                return Optional.of(STOSD);
            }
        } else {
            if (CMPSB.getOpCode().equals(opcode)) {
                return Optional.of(CMPSB);
            } else if (LODSB.getOpCode().equals(opcode)) {
                return Optional.of(LODSB);
            } else if (STOSB.getOpCode().equals(opcode)) {
                return Optional.of(STOSB);
            }
        }
        return Optional.ofNullable(bitsToOpCode.get(opcode));
    }

    private static final Map<String, Op> memToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    private static final Map<String, Op> bitsToOpCode = Stream.of(OpCode.values())
            .collect(Collectors.toMap(Op::getOpCode, Function.identity(), (op1, op2) -> op2));

    public static Optional<Op> fromMem(final String mem) {
        return Optional.ofNullable(memToOpCode.get(mem.toUpperCase()));
    }
}
