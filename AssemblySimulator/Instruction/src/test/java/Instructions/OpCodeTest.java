package Instructions;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static Instructions.OpCode.*;


public class OpCodeTest {
    @Test
    public void opCodeIsValid() {
        OpCode mov = OpCode.MOV;
        Assert.assertEquals("000110", mov.getOpCode());
    }

    @Test
    public void getOpCodeFromValue(){
        Op LODSB = OpCode.valueOf("LODSB");
        Assert.assertEquals(OpCode.LODSB, LODSB);

    }

    @Test
    public void getOpCodeFromStr(){
        Optional<Op> repne = OpCode.fromString("REPNE");
        Assert.assertEquals(Optional.of(REPNE),repne);
    }

    @Test
    public void getOpCodeFromBinary(){
        final Optional<Op> op = OpCode.of("100000");
        Assert.assertEquals(Optional.of(JCC),op);
        Assert.assertEquals("100000",op.map(x->x.getOpCode()).orElseGet(() -> ""));
    }

    @Test
    public void allOpCodesHaveDistinctBinaryValues(){
        long opSize = OpCode.values().length;
        long binarySize = Stream.of(OpCode.values()).map(OpCode::getOpCode).distinct().count();
        Assert.assertEquals(opSize,binarySize);
    }

}