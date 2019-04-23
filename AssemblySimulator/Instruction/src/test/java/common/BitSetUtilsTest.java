package common;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;

public class BitSetUtilsTest {
    @Test
    public void ConvertFromStringTest() throws Exception{
        final String str = "010101";
        BitSet bitSet = BitSetUtils.fromString(str);
        String result = BitSetUtils.toString(bitSet, 6);
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
            final String to = BitSetUtils.toString(bitSet, 6);
            Assert.assertEquals(from, to);
        }
    }
}
