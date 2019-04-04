package Instructions;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EightBitsRegister implements Register {
    AL("000"),
    CL("001"),
    DL("010"),
    BL("011"),
    AH("100"),
    CH("101"),
    DH("110"),
    BH("111");
    private final String registerCode;

    EightBitsRegister(String code) {
        registerCode = code;
    }

    @Override
    public RegisterLength getRegisterLength() {
        return RegisterLength.EIGHT;
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
