package Instructions;

import common.BitSetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The instruction class.
 * All source code will convert to consecutive instruction objects.
 */
public class Instruction {
    Logger LOGGER = Logger.getLogger(Instruction.class);
    final private Op opcode;
    final private Operand register;
    final private Operand memRegister;
    final private boolean isFromMemToReg;
    final private boolean isEightBitsRegister;
    final private boolean isSixteenBitsRegister;

    static Set<String> givenLengthOp = new HashSet<>();

    static {
        givenLengthOp.add("CMPSB");
        givenLengthOp.add("CMPSW");
        givenLengthOp.add("CMPSD");
        givenLengthOp.add("LODSB");
        givenLengthOp.add("LODSW");
        givenLengthOp.add("LODSD");
        givenLengthOp.add("STOSB");
        givenLengthOp.add("STOSW");
        givenLengthOp.add("STOSD");
    }

    public boolean isEightBitsRegister() {
        return isEightBitsRegister;
    }

    public boolean isFromMemToReg() {
        return isFromMemToReg;
    }

    public Operand getMemRegister() {
        return memRegister;
    }

    public Op getOpcode() {
        return opcode;
    }

    public Operand getRegister() {
        return register;
    }

    public Instruction(Op op, Operand destination, Operand source) throws Exception {
        opcode = op;
        if (Mode.REGISTER.equals(source.getMode())) {
            isFromMemToReg = false;
            register = source;
            memRegister = destination;
        } else if (Mode.REGISTER.equals(destination.getMode())) {
            isFromMemToReg = true;
            register = destination;
            memRegister = source;
        } else {
            if (Mode.IMMEDIATE.equals(source.getMode())) {
                isFromMemToReg = false;
                register = source;
                memRegister = destination;
            } else {
                throw new Exception("there must be at least one register operand in source or destination. Or there is a immediate number in source");
            }
        }
        if (Mode.IMMEDIATE.equals(register.getMode())) {
            if (Mode.REGISTER.equals(memRegister.getMode())) {
                if (RegisterLength.EIGHT.equals(memRegister.getRegister().getRegisterLength())) {
                    isEightBitsRegister = true;
                    isSixteenBitsRegister = false;
                } else if (RegisterLength.SIXTEEN.equals(memRegister.getRegister().getRegisterLength())) {
                    isSixteenBitsRegister = true;
                    isEightBitsRegister = false;
                } else {
                    isSixteenBitsRegister = false;
                    isEightBitsRegister = false;
                }
            } else if (givenLengthOp.contains(opcode.getMemonic())) {
                if ("CMPSB".equals(opcode.getMemonic()) || "LODSB".equals(opcode.getMemonic()) || "STOSB".equals(opcode.getMemonic())) {
                    isEightBitsRegister = true;
                    isSixteenBitsRegister = false;
                } else if ("CMPSW".equals(opcode.getMemonic()) || "LODSW".equals(opcode.getMemonic()) || "STOSW".equals(opcode.getMemonic())) {
                    isEightBitsRegister = false;
                    isSixteenBitsRegister = true;
                } else {
                    isEightBitsRegister = false;
                    isSixteenBitsRegister = false;
                }
            } else {
                isEightBitsRegister = false;
                isSixteenBitsRegister = false;
                throw new Exception("cannot infer register length from instruction");
            }
        } else {
            if (RegisterLength.EIGHT.equals(register.getRegister().getRegisterLength())) {
                isEightBitsRegister = true;
                isSixteenBitsRegister = false;
            } else if (RegisterLength.SIXTEEN.equals(register.getRegister().getRegisterLength())) {
                isEightBitsRegister = false;
                isSixteenBitsRegister = true;
            } else {
                isSixteenBitsRegister = false;
                isEightBitsRegister = false;
            }
        }


    }

    public Operand getSource() {
        if (isFromMemToReg) {
            return memRegister;
        } else {
            return register;
        }
    }

    public Operand getDestination() {
        if (isFromMemToReg) {
            return register;
        } else {
            return memRegister;
        }
    }

    public byte[] toBytes() throws Exception {
        StringBuilder builder = new StringBuilder();
        if (isSixteenBitsRegister) {
            builder.append("11111111");
            // Intel use 0x66, However, since 0x66 represent
        }
        //To simplify, use string manipulate here.
        //Need make it more efficient later.
        boolean isImmediate = false;
        Mode mode = getMemRegister().getMode();
        if (Mode.IMMEDIATE.equals(getRegister().getMode())) {
            isImmediate = true;
        }

        if (isImmediate) {
            String opStr = "1" + opcode.getOpCode().substring(1);
            builder.append(opStr);
        } else {
            builder.append(opcode.getOpCode());
        }
        builder.append(isFromMemToReg ? '1' : '0');
        builder.append(isEightBitsRegister ? '0' : '1');
        boolean oneByteDisplacement = false;
        if (getMemRegister().getDisplacement() >= -128 && getMemRegister().getDisplacement() <= 127) {
            oneByteDisplacement = true;
        }
        boolean isSIB = false;
        boolean isDisplacement = false;
        switch (mode) {
            case REGISTER:
                builder.append("11");
                break;
            case DISPLACEMENT_ONLY:
                builder.append("00");
                isDisplacement = true;
                break;
            case INDIRECT:
                builder.append("00");
                break;
            case SIB:
                builder.append("00");
                isSIB = true;
                break;
            case IMMEDIATE:
                builder.append("00");
                break;
            case SIB_DISPLACEMENT_FOLLOWED:
                isSIB = true;
                isDisplacement = true;
                if (oneByteDisplacement) {
                    builder.append("01");
                } else {
                    builder.append("10");
                }
                break;
            case INDIRECT_DISPLACEMENT_FOLLOWED:
                isDisplacement = true;
                if (oneByteDisplacement) {
                    builder.append("01");
                } else {
                    builder.append("10");
                }
                break;
        }
        if (isImmediate) {
            builder.append("000");
        } else {
            builder.append(register.getRegister().getRegisterCode());
        }
        if (Mode.DISPLACEMENT_ONLY.equals(mode)) {
            builder.append("101");
        } else if (isSIB) {
            builder.append("100");
        } else {
            builder.append(memRegister.getRegister().getRegisterCode());
        }
        if (isSIB) {
            switch (memRegister.getScale()) {
                case 1:
                    builder.append("00");
                    break;
                case 2:
                    builder.append("01");
                    break;
                case 4:
                    builder.append(("10"));
                    break;
                case 8:
                    builder.append("11");
                    break;
            }
            builder.append(memRegister.getIndex().getRegisterCode());
            builder.append(memRegister.getBase().getRegisterCode());
        }
        if (isDisplacement) {
            String displacement = Integer.toBinaryString(memRegister.getDisplacement());
            if (oneByteDisplacement) {
                for (int i = 0; i < 8 - displacement.length(); i++) {
                    builder.append('0');
                }
            } else {
                for (int i = 0; i < 32 - displacement.length(); i++) {
                    builder.append('0');
                }
            }
            builder.append(displacement);
        }
        if (isImmediate) {
            String immediate = Integer.toBinaryString(register.getImmediate());
            if (isEightBitsRegister) {
                if (immediate.length() > 8) {
                    throw new Exception("immediate " + register.getImmediate() + " is larger then 8 bits");
                }
                for (int i = 0; i < 8 - immediate.length(); i++) {
                    builder.append('0');
                }
            } else if (isSixteenBitsRegister) {
                if (immediate.length() > 16) {
                    throw new Exception("immediate " + register.getImmediate() + " is larger then 16 bits");
                }
                for (int i = 0; i < 16 - immediate.length(); i++) {
                    builder.append('0');
                }
            } else {
                if (immediate.length() > 32) {
                    throw new Exception("immediate " + register.getImmediate() + " is larger then 32 bits");
                }
                for (int i = 0; i < 32 - immediate.length(); i++) {
                    builder.append('0');
                }
            }
            builder.append(immediate);
        }
        LOGGER.info("create instruction " + builder.toString());
        final String instructionStr = builder.toString();
        assert instructionStr.length() % 8 == 0;
        return BitSetUtils.fromBinaryStringToByteArray(instructionStr);
        /*BitSet bitSet = BitSetUtils.fromString(builder.toString());
        byte[] result = bitSet.toByteArray();
        ArrayUtils.reverse(result);
        */
        //return result;
    }
}
