package Instructions;

import common.BitSetUtils;

import java.util.BitSet;
import java.util.Optional;

/**
 * An interface which represent all 8 bits registers, 16 bits registers and 32 bits registers.
 */
public interface Register {
    int CODE_SIZE = 3;

    RegisterLength getRegisterLength();


    String getRegisterName();

    String getRegisterCode();

    default BitSet getReigisterCodeBits(){
        return BitSetUtils.fromString(getRegisterCode());
    }

    static Optional<Register> of(RegisterLength length, String code) {
        if (length == RegisterLength.EIGHT) {
            return EightBitsRegister.of(code);
        } else if (length == RegisterLength.SIXTEEN) {
            return SixteenBitsRegister.of(code);
        } else {
            return ThirtyTwoBitsRegister.of(code);
        }
    }
    static Optional<Register> fromName(String name){
        if(EightBitsRegister.fromName(name).isPresent()){
            return EightBitsRegister.fromName(name);
        }
        else if(SixteenBitsRegister.fromName(name).isPresent()){
            return SixteenBitsRegister.fromName(name);
        }
        else{
            return ThirtyTwoBitsRegister.fromName(name);
        }
    }
}
