package common;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Utils to convert string to/from BitSet.
 */
public class BitSetUtils {
    private static final Logger LOGGER = Logger.getLogger(BitSetUtils.class);

    public static BitSet fromString(final String s) throws Exception {
        final String tmp = new StringBuilder(s).reverse().toString();
        // The reason for reversing is because for bitset, the right most digit represents index 0.
        // However, for string, the left most digit represents index 0;
        BitSet bitSet = new BitSet();
        for (int i = 0; i < tmp.length(); i++) {
            if (tmp.charAt(i) == '1') {
                bitSet.set(i, true);
            } else if (tmp.charAt(i) != '0') {
                String exceptionMsg = "the character at position " + i + " of string " + s.substring(0, s.length() - i - 1) + "(" + s.charAt(s.length() - i - 1) + ")" + s.substring(s.length() - i) + " is either 0 nor 1";
                throw new Exception(exceptionMsg);
            }
        }
        return bitSet;
    }

    public static String toString(final BitSet bitSet) {
        if (bitSet.isEmpty()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            byte[] result = bitSet.toByteArray();
            for (int i = result.length - 1; i >= 0; i--) {
                builder.append(Integer.toBinaryString((result[i] & 0xFF) + 0x100).substring(1));
            }
            return StringUtils.stripStart(builder.toString(),"0");
        }

    }

    public static String toString(byte x){
        return Integer.toBinaryString((x& 0xFF) + 0x100).substring(1);
    }

    public static byte fromBinaryStringToByte(final String str) throws Exception {
        assert str.length() <= 8;
        int j = 1;
        byte ret = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '1') {
                ret |= j;
            } else if (str.charAt(i) != '0') {
                throw new Exception("not a valid input string");
            }
            j = j << 1;
        }
        return ret;
    }

    public static byte[] fromBinaryStringToByteArray(final String str) throws Exception {
        if (StringUtils.isBlank(str)) {
            return new byte[0];
        }
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i += 8) {
            String tmp = str.substring(i, Math.min(i + 8, str.length()));
            list.add(fromBinaryStringToByte(tmp));
        }
        byte[] ret = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static String toString(final BitSet bitSet, int size) throws Exception {
        if (bitSet.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                stringBuilder.append('0');
            }
            LOGGER.debug("return " + stringBuilder.toString());
            return stringBuilder.toString();
        } else {
            final String value = toString(bitSet);
            LOGGER.debug("convert value " +value);
            if (value.length() == size) {
                return value;
            } else if (value.length() < size) {
                StringBuilder stringBuilder = new StringBuilder(value);
                for (int i = 0; i < size - value.length(); i++) {
                    stringBuilder.insert(0, '0');
                }
                LOGGER.debug("return " + stringBuilder.toString());
                return stringBuilder.toString();

            } else {
                throw new Exception("the size " + size + " is too small for converting");
            }
        }
    }
}
