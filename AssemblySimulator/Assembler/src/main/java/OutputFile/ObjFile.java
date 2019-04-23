package OutputFile;

/**
 * The output binary file
 */
public class ObjFile implements SupportTwoParsingPass{
    DataSegment dataSegment = new DataSegment();

    public DataSegment getDataSegment() {
        if(dataSegment==null){
            dataSegment = new DataSegment();
        }
        return dataSegment;
    }


    @Override
    public void resetAfterFirstParsingPass(){
        DataSegment dataSegment = getDataSegment();
        dataSegment.resetAfterFirstParsingPass();
    }
}
