package Instructions;

/**
 * The instruction class.
 * All source code will convert to consecutive instruction objects.
 */
public class Instruction {
    final private Op opcode;
    final private Operand register;
    final private Operand memRegister;
    final private boolean isFromMemToReg;
    final private boolean isEightBitsRegister;

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

    public Instruction(Op op, Operand from, Operand to) throws Exception {
        opcode = op;
        if (Mode.REGISTER.equals(from.getMode())) {
            isFromMemToReg = false;
            register = from;
            memRegister = to;
        } else if (Mode.REGISTER.equals(to.getMode())) {
            isFromMemToReg = true;
            register = to;
            memRegister = from;
        } else {
            throw new Exception("there must be at least one register operand in from and to");
        }
        if (RegisterLength.EIGHT.equals(register.getRegister().getRegisterLength())) {
            isEightBitsRegister = true;
        } else {
            isEightBitsRegister = false;
        }
    }

    public Operand getFrom() {
        if (isFromMemToReg) {
            return memRegister;
        } else {
            return register;
        }
    }

    public Operand getTo() {
        if (isFromMemToReg) {
            return register;
        } else {
            return memRegister;
        }
    }

    public byte[] toBytes() {
        throw new UnsupportedOperationException();
    }
}
