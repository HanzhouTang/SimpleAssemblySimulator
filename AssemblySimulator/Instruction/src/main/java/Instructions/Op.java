package Instructions;

import org.apache.log4j.Logger;

import java.util.BitSet;

public interface Op {
    final static int SIZE = 6;
    final static Logger OPLOGGER = Logger.getLogger(Op.class);

    public String getMemonic();

    public BitSet getBits();

    public String getOpCode();

    public default  Logger getOpLogger(){
        return OPLOGGER;
    }
}
