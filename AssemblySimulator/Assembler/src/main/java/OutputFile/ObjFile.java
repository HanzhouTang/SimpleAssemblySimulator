package OutputFile;

public class ObjFile {
    DataSegment dataSegment = new DataSegment();

    public DataSegment getDataSegment() {
        if(dataSegment==null){
            dataSegment = new DataSegment();
        }
        return dataSegment;
    }
}
