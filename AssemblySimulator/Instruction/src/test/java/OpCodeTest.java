import Instructions.Op;
import Instructions.OpCode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static Instructions.OpCode.*;


public class OpCodeTest {
    @Test
    public void opCodeIsValid(){
        OpCode mov = OpCode.MOV;
        Assert.assertEquals("000000",mov.getOpCodeStr());
    }

    @Test
    public void getOpCodeFromValue(){
        Op LODSB = OpCode.valueOf("LODSB");
        final String opCodeStr = LODSB.getOpCodeStr();
        Assert.assertEquals("010100", opCodeStr);

    }

    @Test
    public void getOpCodeFromStr(){
        Optional<Op> repne = OpCode.fromString("REPNE");
        Assert.assertEquals(Optional.of(REPNE),repne);
    }

    @Test
    public void getBinaryStrFromOpCode(){
        final String bits = CMPSD.getOpCodeStr();
        Assert.assertEquals("010011",bits);
    }

    @Test
    public void getOpCodeFromBinary(){
        final Optional<Op> op = OpCode.of("100000");
        Assert.assertEquals(Optional.of(RET),op);
        Assert.assertEquals("100000",op.map(x->x.getOpCodeStr()).orElseGet(() -> ""));
    }

}
