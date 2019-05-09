package tomasulo;

import Instructions.Mode;
import Instructions.Operand;

public class DependencyFactory {
    public static Dependency createDependency(Operand operand) {
        if (Mode.INDIRECT.equals(operand.getMode()) ||
                Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(operand.getMode())) {
            return new IndirectDependency(operand.getDisplacement(), operand.getBase());
        } else if (Mode.DISPLACEMENT_ONLY.equals(operand.getMode())) {
            return new IndirectDependency(operand.getDisplacement(), null);
        } else if (Mode.SIB.equals(operand.getMode()) || Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(operand.getMode())) {
            return new SibDependency(operand.getIndex(), operand.getBase(), operand.getScale(), operand.getDisplacement());
        }
        return null;
    }
}
