package Instructions;


import javax.naming.OperationNotSupportedException;

/**
 * Build wrapper for operand.
 *
 * @author Hanzhou Tang
 */
public class Operand {

    final private Mode mode;
    final private int displacement;
    final private Register base;
    final private Register index;
    final private int scale;

    public Mode getMode() {
        return mode;
    }

    public int getDisplacement() {
        return displacement;
    }

    public Register getBase() {
        return base;
    }

    public Register getIndex() {
        return index;
    }

    public int getScale() {
        return scale;
    }

    public int getImmediate() {
        return displacement;
    }

    public Register getRegister() throws Exception {
        if (Mode.REGISTER.equals(mode) || Mode.INDIRECT.equals(mode)||Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode)) {
            return base;
        }
        throw new OperationNotSupportedException();
    }

    public Operand(Builder builder) {
        this.mode = builder.mode;
        this.displacement = builder.displacement;
        this.base = builder.base;
        this.index = builder.index;
        this.scale = builder.scale;
    }

    public static class Builder {
        private Mode mode = null;
        private Register base;
        private Register index;
        private int scale = 0;
        private int displacement = 0;

        public Operand build() {
            return new Operand(this);
        }

        public Builder immediate(int i) throws Exception {
            if (mode != null && !Mode.IMMEDIATE.equals(mode)) {
                throw new Exception("Immediate mode cannot be together with " + mode.name());
            }
            displacement = i;
            return this;
        }

        public Builder sib(Register b, Register i, int s) throws Exception {
            if (mode != null && !Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) && !Mode.SIB.equals(mode)) {
                throw new Exception("SIB mode cannot be together with {} " + mode.name());
            }
            base = b;
            index = i;
            if (s != 1 && s != 2 && s != 4 && s != 8) {
                throw new Exception("scale must be 1, 2, 4 or 8");
            }
            scale = s;
            return this;
        }

        public Builder indirect(Register r) throws Exception {
            if (Mode.SIB.equals(mode) || Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.REGISTER.equals(mode) || Mode.IMMEDIATE.equals(mode)) {
                throw new Exception("Indirect mode cannot be together with " + mode.name());
            } else if (Mode.DISPLACEMENT_ONLY.equals(mode)) {
                mode = Mode.INDIRECT_DISPLACEMENT_FOLLOWED;
            } else if (!Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode)) {
                mode = Mode.INDIRECT;
            }
            base = r;
            return this;
        }

        public Builder displacement(int number) throws Exception {
            if (Mode.IMMEDIATE.equals(mode)) {
                throw new Exception("Immediate mode cannot have displacement");
            } else if (Mode.REGISTER.equals(mode)) {
                throw new Exception("Register mode cannot have displacement");
            } else if (Mode.INDIRECT.equals(mode)) {
                mode = Mode.INDIRECT_DISPLACEMENT_FOLLOWED;
            } else if (Mode.SIB.equals(mode)) {
                mode = Mode.SIB_DISPLACEMENT_FOLLOWED;
            } else if (!Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) && !Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode)) {
                mode = Mode.DISPLACEMENT_ONLY;
            }
            this.displacement = number;
            return this;
        }

        public Builder register(Register r) throws Exception {
            if (mode != null && (!Mode.REGISTER.equals(mode))) {
                throw new Exception("Register mode cannot be together with " + mode.name());
            } else {
                mode = Mode.REGISTER;
            }
            base = r;
            return this;
        }
    }
}
