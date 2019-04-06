package assembler;

public class SourceCodeWrapper {
    final private Integer lineNumber;
    final private String code;
    public SourceCodeWrapper(Integer i, String s){
        lineNumber = i;
        code = s;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }
    public String getCode(){
        return code;
    }
}
