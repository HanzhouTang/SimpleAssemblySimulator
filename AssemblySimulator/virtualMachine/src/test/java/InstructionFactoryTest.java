import Instructions.Instruction;
import Instructions.OpCode;
import instructions.InstructionBase;
import instructions.InstructionFactory;
import instructions.NopInstruction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import virtualmachine.ClockCycleCounter;

import static org.hamcrest.CoreMatchers.instanceOf;

@SpringBootTest
public class InstructionFactoryTest {
    @Test
    public void getNopInstructionByFactory() throws Exception {
        Instruction nop = new Instruction(OpCode.NOP, null, null);
        InstructionBase nopInstruction = InstructionFactory.createInstruction(nop, 0, 1, 1,  null);
        Assert.assertNotNull(nopInstruction);
        Assert.assertThat(nopInstruction, instanceOf(NopInstruction.class));
    }
}
