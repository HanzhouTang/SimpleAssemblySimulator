package Instructions;

import java.util.Optional;

public interface Register {
    int CODE_SIZE = 3;

    RegisterLength getRegisterLength();

    String getRegisterName();

    String getRegisterCode();

    static Optional<Register> of(RegisterLength length, String code) {
        if (length == RegisterLength.EIGHT) {
            return EightBitsRegister.of(code);
        } else if (length == RegisterLength.SIXTEEN) {
            return SixteenBitsRegister.of(code);
        } else {
            return ThirtyTwoBitsRegister.of(code);
        }
    }
}
