package Instructions;

import org.apache.log4j.Logger;

import java.util.BitSet;

public class BitSetUtils {
    private static final Logger LOGGER = Logger.getLogger(BitSetUtils.class);

    public static BitSet fromString(final String s) {
        final String tmp = new StringBuilder(s).reverse().toString();
        BitSet bitSet = new BitSet();
        for (int i = 0; i < tmp.length(); i++) {
            if (tmp.charAt(i) == '1') {
                bitSet.set(i, true);
            } else if (tmp.charAt(i) != '0') {
                LOGGER.warn("the character at position " + i + " of string " + s + " is either 0 nor 1");
            }
        }
        return bitSet;
    }

    public static String toString(final BitSet bitSet) {
        if (bitSet.isEmpty()) {
            return "";
        } else {
            return Long.toString(bitSet.toLongArray()[0], 2);
        }

    }

    public static String toString(final BitSet bitSet, int size) {
        if (bitSet.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                stringBuilder.append('0');
            }
            return stringBuilder.toString();
        } else {
            final String value = toString(bitSet);
            if (value.length() == size) {
                return value;
            } else if (value.length() < size) {
                StringBuilder stringBuilder = new StringBuilder(value);
                for (int i = 0; i < size - value.length(); i++) {
                    stringBuilder.insert(0, '0');
                }
                return stringBuilder.toString();
            } else {
                return value.substring(value.length() - size);
            }
        }
    }
}
