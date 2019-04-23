package assembler;

import Instructions.Register;
import OutputFile.OperandWrapper;
import org.junit.Assert;
import org.junit.Test;

public class OperandWrapperTest {
    @Test
    public void buildIndirectDisplacementTest() throws Exception{
        Register eax = Register.fromName("eax").get();
        OperandWrapper wrapper = new OperandWrapper.Builder().indirect(eax).displacement(12).build();
        Assert.assertEquals(wrapper.getMode(), OperandWrapper.Mode.INDIRECT_DISPLACEMENT_FOLLOWED);
    }
}
