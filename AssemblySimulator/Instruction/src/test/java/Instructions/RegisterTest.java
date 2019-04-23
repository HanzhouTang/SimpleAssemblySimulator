package Instructions;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class RegisterTest {
    @Test
    public void getRegister() {
        Optional<Register> eax = Register.of(RegisterLength.THIRY_TWO,"000");
        Assert.assertEquals(Optional.of(ThirtyTwoBitsRegister.EAX),eax);
    }
    @Test
    public void getRegisterFromName(){
        Optional<Register> eax = Register.fromName("eax");
        Assert.assertEquals(Optional.of(ThirtyTwoBitsRegister.EAX),eax);
    }
    @Test
    public void getNullRegister(){
        Optional<Register> eax = SixteenBitsRegister.fromName("eax");
        Assert.assertEquals(Optional.ofNullable(null),eax);
    }

}
