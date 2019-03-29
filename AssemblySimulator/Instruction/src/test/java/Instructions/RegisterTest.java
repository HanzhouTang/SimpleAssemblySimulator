package Instructions;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class RegisterTest {
    @Test
    public void GetRegister() {
        Optional<Register> eax = Register.of(RegisterLength.THIRY_TWO,"000");
        Assert.assertEquals(Optional.of(ThirtyTwoBitsRegister.EAX),eax);
    }
}
