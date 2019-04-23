package Instructions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Mode {
    INDIRECT_SIB_DISPLACEMENT("00"),
    ONE_BYTE_DISPLACEMENT("01"),
    FOUR_BYTE_DISPLACEMENT("10"),
    REGISTER("11");
    private String bits;
    Mode(String s){
        bits = s;
    }
    public String getBits(){
        return bits;
    }

    static private Map<String,Mode> str2Mode = Stream.of(values()).collect(Collectors.toMap(Mode::getBits, Function.identity()));

    static public Optional<Mode> of(String s){
        return Optional.ofNullable(str2Mode.get(s));
    }

}
