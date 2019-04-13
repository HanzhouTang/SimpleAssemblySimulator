package Instructions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * All registers contain 32 bits data.
 */
public enum ThirtyTwoBitsRegister implements Register{
    EAX("000"),
    ECX("001"),
    EDX("010"),
    EBX("011"),
    ESP("100"),
    EBP("101"),
    ESI("110"),
    EDI("111");
    private final String registerCode;

    ThirtyTwoBitsRegister(String code) {
        registerCode = code;
    }

    @Override
    public RegisterLength getRegisterLength() {
        return RegisterLength.THIRY_TWO;
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
        return Optional.ofNullable(nameToRegister.get(name.toLowerCase()));
    }
}
