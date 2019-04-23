package Instructions;

import org.junit.Assert;
import org.junit.Test;

public class OperandTest {
    @Test
    public void buildIndirectDisplacementTest() throws Exception{
        Register eax = Register.fromName("eax").get();
        Operand wrapper = new Operand.Builder().indirect(eax).displacement(12).build();
        Assert.assertEquals(wrapper.getMode(), Operand.Mode.INDIRECT_DISPLACEMENT_FOLLOWED);
    }
}
