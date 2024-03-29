package Instructions;

import org.apache.log4j.Logger;

import java.util.BitSet;

/**
 * An interface of Op.
 * Basic, all op should be able to convert to/from BitSet.
 */
public interface Op {
    int SIZE = 6;
    Logger OPLOGGER = Logger.getLogger(Op.class);

    String getMemonic();

    BitSet getBits();

    String getOpCode();

    default Logger getOpLogger() {
        return OPLOGGER;
    }
}
