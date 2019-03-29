package Instructions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SixteenBitsRegister  implements  Register{
    AX("000"),
    CX("001"),
    DX("010"),
    BX("011"),
    SP("100"),
    BP("101"),
    SI("110"),
    DI("111");
    private final String registerCode;

    SixteenBitsRegister(String code) {
        registerCode = code;
    }

    @Override
    public RegisterLength getRegisterLength() {
        return RegisterLength.SIXTEEN;
    }

    @Override
    public String getRegisterName() {
        return name().toLowerCase();
    }

    @Override
    public String getRegisterCode() {
        return registerCode;
    }

    private static Map<String, Register> codeToRegister =
            Stream.of(values()).collect(Collectors.toMap(Register::getRegisterCode, Function.identity()));

    public static Optional<Register> of(String registerCode) {
        return Optional.ofNullable(codeToRegister.get(registerCode));
    }

    private static Map<String, Register> nameToRegister =
            Stream.of(values()).collect(Collectors.toMap(Register::getRegisterName, Function.identity()));

    public static Optional<Register> fromName(String name) {
        return Optional.ofNullable(nameToRegister.get(name));
    }
}
