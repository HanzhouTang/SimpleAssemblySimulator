package OutputFile;

import common.BitSetUtils;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * The output binary file
 */
public class ObjFile implements SupportTwoParsingPass {
    private static final Logger LOGGER = Logger.getLogger(ObjFile.class);
    DataSegment dataSegment = null;

    CodeSegment codeSegment = null;

    public CodeSegment getCodeSegment() {
        if (codeSegment == null) {
            codeSegment = new CodeSegment();
        }
        return codeSegment;
    }

    public DataSegment getDataSegment() {
        if (dataSegment == null) {
            dataSegment = new DataSegment();
        }
        return dataSegment;
    }


    @Override
    public void resetAfterFirstParsingPass(Object... params) throws Exception {
        int dataSegmentLength = getDataSegment().getCurrentLocation();
        getDataSegment().resetAfterFirstParsingPass();
        getCodeSegment().resetAfterFirstParsingPass(dataSegmentLength);
    }

    public void dump(String filename) throws Exception {
        if (codeSegment == null) {
            throw new Exception("must contain code segment");
        }
        LOGGER.info("Base address:     \t" + getCodeSegment().getBaseAddress());
        LOGGER.info("Entry point:      \t" + getCodeSegment().getEntryPoint());
        LOGGER.info("Entry procedure:  \t" + getCodeSegment().getEntryPointProcedure());
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(filename))) {
            dataOutputStream.writeInt(0X7f);// header, indicate this is a obj file
            dataOutputStream.writeInt(getCodeSegment().getBaseAddress());// base address
            dataOutputStream.writeInt(getCodeSegment().getEntryPoint());// entry address

            byte[] data = BitSetUtils.toByteArray(getDataSegment().getData());
            byte[] code = BitSetUtils.toByteArray(getCodeSegment().getCode());
            dataOutputStream.write(data, 0, data.length);// data segment
            dataOutputStream.write(code, 0, code.length);// code segment
        } catch (Exception e) {
            throw e;
        }
    }
}
