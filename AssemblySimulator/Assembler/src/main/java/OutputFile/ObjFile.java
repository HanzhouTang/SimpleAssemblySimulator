package OutputFile;

/**
 * The output binary file
 */
public class ObjFile implements SupportTwoParsingPass {
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
}
