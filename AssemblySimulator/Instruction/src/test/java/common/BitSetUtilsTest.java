package common;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;


public class BitSetUtilsTest {
    private static final Logger LOGGER = Logger.getLogger(BitSetUtilsTest.class);

    @Test
    public void ConvertFromStringTest() throws Exception{
        final String str = "010101";
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet, 8);
        Assert.assertEquals("00010101", result);
    }

    @Test
    public void ConvertByteToString(){
        Byte b = -128;
        Assert.assertEquals("10000000",BitSetUtils.toString(b.byteValue()));
    }

    @Test
    public  void TwoComplement(){
        String str = "10000000000000000000000000000000";
        Assert.assertEquals(-2147483648,BitSetUtils.getTwosComplement(str));
    }

    @Test
    public void Convert128BitsFromStringTest() throws Exception{
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<128;i++){
            if(Math.random()<0.5){
                builder.append(0);
            }
            else{
                builder.append(1);
            }
        }
        final String str = builder.toString();
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet,128);
        Assert.assertEquals(str, result);
    }
    @Test
    public void Convert128BitsFromStringTest1() throws Exception{
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<64;i++){
           builder.append(0);
        }
        final String tmp = builder.toString();
        final String str = tmp.substring(1) + "1"+tmp;
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet,128);
        Assert.assertEquals(str, result);
    }

    @Test(expected = Exception.class)
    public void ConvertFromStringTest2() throws Exception {
        final String str = "010110";
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet, 3);
    }

    @Test
    public void ConvertToStringTest() throws Exception {
        BitSet bitSet = new BitSet();
        bitSet.clear();
        final String result = BitSetUtils.toString(bitSet);
        Assert.assertEquals("", result);
        final String result1 = BitSetUtils.toString(bitSet, 6);
        Assert.assertEquals("000000", result1);
    }

    @Test
    public void ConverTest() throws Exception {
        String[] tests = {"100000","100001","100010"};
        for (String from : tests) {
            BitSet bitSet = BitSetUtils.fromString(from);
            final String to = BitSetUtils.toString(bitSet, 8);
            Assert.assertEquals("00"+from, to);
        }
    }
}
