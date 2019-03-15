import Instructions.BitSetUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

public class BitSetUtilsTest {
    @Before
    public void setup(){
        BasicConfigurator.configure();
    }
    @Test
    public void ConvertFromStringTest(){
        final String str = "010101";
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet,6);
        Assert.assertEquals(str,result);
    }
    @Test
    public void ConvertFromStringTest2(){
        final String str = "010110";
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet,3);
        Assert.assertEquals("110",result);
    }
    @Test
    public void ConvertToStringTest(){
        BitSet bitSet = new BitSet();
        bitSet.clear();
        final  String result = BitSetUtils.toString(bitSet);
        Assert.assertEquals("0",result);
        final  String result1 = BitSetUtils.toString(bitSet,6);
        Assert.assertEquals("000000",result1);
    }
}
