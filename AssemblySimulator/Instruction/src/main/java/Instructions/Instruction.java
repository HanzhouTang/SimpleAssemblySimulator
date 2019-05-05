package Instructions;

import common.BitSetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * The instruction class.
 * All source code will convert to consecutive instruction objects.
 * For one operand instruction, always set source.
 * TODO instead directly refer field, using get method.
 */
public class Instruction {
    private static Logger LOGGER = Logger.getLogger(Instruction.class);
    final private Op opcode;
    final private Operand register;
    final private Operand memRegister;
    final private boolean isFromMemToReg;
    final private boolean isEightBitsRegister;
    final private boolean isSixteenBitsRegister;

    private static Set<String> givenLengthOp = new HashSet<>();

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
        if (source != null && Mode.IMMEDIATE.equals(source.getMode())) {
            LOGGER.debug("immediate source");
            isFromMemToReg = false;
            register = source;
            memRegister = destination;
        } else if (source != null && Mode.REGISTER.equals(source.getMode())) {
            isFromMemToReg = false;
            register = source;
            memRegister = destination;
        } else if (destination != null && Mode.REGISTER.equals(destination.getMode())) {
            isFromMemToReg = true;
            register = destination;
            memRegister = source;
        } else if (destination == null && source == null) {
            isFromMemToReg = false;
            register = null;
            memRegister = null;
        } else {
            throw new Exception("there must be at least one register operand in source or destination. Or there is a immediate number in source");
        }
        if (register != null && Mode.IMMEDIATE.equals(register.getMode())) {
            if (memRegister != null && Mode.REGISTER.equals(memRegister.getMode())) {
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
                //throw new Exception("cannot infer register length from instruction");
            }
        } else {
            if (register != null && RegisterLength.EIGHT.equals(register.getRegister().getRegisterLength())) {
                isEightBitsRegister = true;
                isSixteenBitsRegister = false;
            } else if (register != null && RegisterLength.SIXTEEN.equals(register.getRegister().getRegisterLength())) {
                isEightBitsRegister = false;
                isSixteenBitsRegister = true;
            } else {
                isSixteenBitsRegister = false;
                isEightBitsRegister = false;
            }
        }
        if (register != null && memRegister != null) {
            if (Mode.REGISTER.equals(register.getMode()) && Mode.REGISTER.equals(memRegister.getMode())) {
                if (!register.getRegister().getRegisterLength().equals(memRegister.getRegister().getRegisterLength())) {
                    throw new Exception("the dest " + getDestination().getRegister() +
                            " register and source register " + getSource().getRegister() + " must have the same length");
                }
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
        Mode mode = null;
        if (getMemRegister() != null) {
            mode = getMemRegister().getMode();
        }
        if (getRegister() != null && Mode.IMMEDIATE.equals(getRegister().getMode())) {
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
        if (getMemRegister() != null) {
            if (getMemRegister().getDisplacement() >= -128 && getMemRegister().getDisplacement() <= 127) {
                oneByteDisplacement = true;
            }
        }

        boolean isSIB = false;
        boolean isDisplacement = false;
        if (mode != null) {
            LOGGER.debug("mode " + mode.name());
        }
        if (mode != null) {
            switch (mode) {
                case REGISTER:
                    builder.append("11");
                    break;
                case DISPLACEMENT_ONLY:
                    builder.append("00");
                    oneByteDisplacement = false;
                    //for displacement only mode. Always follow a 4 byte displacement.
                    isDisplacement = true;
                    break;
                case INDIRECT:
                    builder.append("00");
                    break;
                case SIB:
                    builder.append("00");
                    isSIB = true;
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
        } else {
            builder.append("00");
            // for instruction like jump
        }

        if (isImmediate) {
            builder.append("000");
        } else {
            if(register!=null){
                builder.append(register.getRegister().getRegisterCode());
            }
            else{
                builder.append("000");
            }

        }
        if (mode == null) {
            builder.append("000");
        } else if (Mode.DISPLACEMENT_ONLY.equals(mode)) {
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
            if (memRegister.getBase() == null) {
                builder.append("101");
            } else {
                builder.append(memRegister.getBase().getRegisterCode());
            }

        }
        if (isDisplacement) {
            LOGGER.debug("one byte displacement " + oneByteDisplacement);

            final String displacement;
            if (oneByteDisplacement) {
                displacement = BitSetUtils.toString((byte) memRegister.getDisplacement());
            } else {
                displacement = Integer.toBinaryString(memRegister.getDisplacement());
            }

            LOGGER.debug("displacement " + displacement);
            char filling = '0';
            if (memRegister.getDisplacement() < 0) {
                filling = '1';
            }
            //logic extend
            if (oneByteDisplacement) {
                for (int i = 0; i < 8 - displacement.length(); i++) {
                    builder.append(filling);
                }
            } else {
                for (int i = 0; i < 32 - displacement.length(); i++) {
                    builder.append(filling);
                }
            }
            LOGGER.debug("displacement " + displacement);
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
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(opcode.toString());
        if(getDestination()!=null){
            builder.append(' ');
            builder.append(getDestination().toString());
        }
        if(getSource()!=null){
            builder.append(", ");
            builder.append(getSource().toString());
        }
        return builder.toString();
    }

    private static String readAndIncreaseAddress(byte[] bytes, MutableInt currentLocation) throws Exception {
        int location = currentLocation.getValue();
        if (location >= bytes.length) {
            throw new Exception("the currentLocation " + currentLocation + " is out bounded");
        }
        currentLocation.add(1);
        return BitSetUtils.toString(bytes[location]);
    }

    private static Register getRegisterFromCode(String code, RegisterLength length) throws Exception {
        Optional<Register> r = Register.of(length, code);
        if (!r.isPresent()) {
            throw new Exception("invalid register code " + code);
        }
        return r.get();
    }

    private static int readDisplacement(byte[] bytes, MutableInt currentLocation, int length) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(readAndIncreaseAddress(bytes, currentLocation));
        }
        final String dis = builder.toString();

        return BitSetUtils.getTwosComplement(dis);
    }

    private static int readImmediate(byte[] bytes, MutableInt currentLocation, int length) throws Exception {
        return readDisplacement(bytes, currentLocation, length);
    }

    private static Operand.Builder readSIB(byte[] bytes, MutableInt currentLocation, Mode mode, RegisterLength length) throws Exception {
        String sib = readAndIncreaseAddress(bytes, currentLocation);
        String scale = sib.substring(0, 2);
        String index = sib.substring(2, 5);
        String base = sib.substring(5);
        if ("100".equals(index)) {
            throw new Exception("100 is invalid for index register");
        }
        Register index_reg = getRegisterFromCode(index, length);
        final Register base_reg;

        if ("101".equals(base) && Mode.SIB.equals(mode)) {
            base_reg = null;
        } else {
            base_reg = getRegisterFromCode(base, length);
        }
        int scale_decimal = (int) Math.pow(2, Integer.parseInt(scale, 2));
        return new Operand.Builder().sib(base_reg, index_reg, scale_decimal);

    }


    public static Instruction fromBytes(byte[] bytes, MutableInt currentLocation) throws Exception {
        String value = readAndIncreaseAddress(bytes, currentLocation);
        boolean isSixTeenRegister = false;
        boolean isImmediate = false;
        if ("11111111".equals(value)) {
            isSixTeenRegister = true;
            value = readAndIncreaseAddress(bytes, currentLocation);
        }
        String opCode = value.substring(0, 6);
        Optional<Op> op = OpCode.of(opCode);
        if (!op.isPresent()) {
            if (opCode.charAt(0) == '1') {
                opCode = "0" + opCode.substring(1);
                isImmediate = true;
                op = OpCode.of(opCode);
            }
            if (!op.isPresent()) {
                throw new Exception("the opcode " + opCode + " is not valid");
            }
        }
        boolean isFromMemToReg = false;
        if (value.charAt(6) == '1') {
            isFromMemToReg = true;
        }
        boolean isEightBitsRegister = false;
        if (value.charAt(7) == '0') {
            isEightBitsRegister = true;
            if (isSixTeenRegister) {
                throw new Exception("16 bits prefix follows a eight bit instruction");
            }
        }
        final RegisterLength length;
        if (isEightBitsRegister) {
            length = RegisterLength.EIGHT;
        } else if (isSixTeenRegister) {
            length = RegisterLength.SIXTEEN;
        } else {
            length = RegisterLength.THIRY_TWO;
        }
        value = readAndIncreaseAddress(bytes, currentLocation);
        final Mode mode;
        String modeStr = value.substring(0, 2);
        String reigsterStr = value.substring(2, 5);
        String memRegisterStr = value.substring(5);
        boolean isDisplacement = false;
        boolean isOneByteDisplacement = false;

        if ("00".equals(modeStr)) {
            if ("100".equals(memRegisterStr)) {
                mode = Mode.SIB;
            } else if ("101".equals(memRegisterStr)) {
                mode = Mode.DISPLACEMENT_ONLY;
            } else {
                mode = Mode.INDIRECT;
            }
        } else if ("01".equals(modeStr)) {
            isDisplacement = true;
            isOneByteDisplacement = true;
            if ("100".equals(memRegisterStr)) {
                mode = Mode.SIB_DISPLACEMENT_FOLLOWED;
            } else {
                mode = Mode.INDIRECT_DISPLACEMENT_FOLLOWED;
            }
        } else if ("10".equals(modeStr)) {
            isDisplacement = true;
            if ("100".equals(memRegisterStr)) {
                mode = Mode.SIB_DISPLACEMENT_FOLLOWED;
            } else {
                mode = Mode.INDIRECT_DISPLACEMENT_FOLLOWED;
            }
        } else {
            mode = Mode.REGISTER;
        }

        final Register register_reg;
        if (!isImmediate) {
            register_reg = getRegisterFromCode(reigsterStr, length);
        } else {
            register_reg = null;
        }

        final Register memRegister_reg;
        if (Mode.DISPLACEMENT_ONLY.equals(mode) || Mode.SIB.equals(mode) || Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode)) {
            memRegister_reg = null;
        } else {
            memRegister_reg = getRegisterFromCode(memRegisterStr, length);
        }
        final Operand memRegister;
        int displacement = 0;
        switch (mode) {
            case INDIRECT_DISPLACEMENT_FOLLOWED:
                displacement = readDisplacement(bytes, currentLocation, isOneByteDisplacement ? 1 : 4);
                memRegister = new Operand.Builder().base(memRegister_reg).displacement(displacement).build();
                break;
            case REGISTER:
                memRegister = new Operand.Builder().register(memRegister_reg).build();
                break;
            case INDIRECT:
                memRegister = new Operand.Builder().base(memRegister_reg).build();
                break;
            case DISPLACEMENT_ONLY:
                displacement = readDisplacement(bytes, currentLocation, 4);
                // for displacement only mode. Always 4 byte displacement
                memRegister = new Operand.Builder().displacement(displacement).build();
                break;
            case SIB:
                LOGGER.debug("sib only");
                memRegister = readSIB(bytes, currentLocation, mode, length).build();
                break;
            case SIB_DISPLACEMENT_FOLLOWED:
                LOGGER.debug("sib with displacement");
                Operand.Builder sibBuilder = readSIB(bytes, currentLocation, mode, length);
                displacement = readDisplacement(bytes, currentLocation, isOneByteDisplacement ? 1 : 4);
                memRegister = sibBuilder.displacement(displacement).build();
                break;
            default:
                memRegister = null;
                break;
        }
        final Operand register;
        if (isImmediate) {
            int immediateLength = 4;
            if (isEightBitsRegister) {
                immediateLength = 1;
            } else if (isSixTeenRegister) {
                immediateLength = 2;
            }
            int immediate = readImmediate(bytes, currentLocation, immediateLength);
            register = new Operand.Builder().immediate(immediate).build();
        } else {
            register = new Operand.Builder().register(register_reg).build();
        }
        Instruction ret = null;
        if (isFromMemToReg) {
            ret = new Instruction(op.get(), register, memRegister);
        } else {
            ret = new Instruction(op.get(), memRegister, register);
        }
        LOGGER.info("convert to instruction { " + ret + " }");
        return ret;
    }
}
