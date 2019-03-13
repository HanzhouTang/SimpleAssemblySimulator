package assembler;



public interface Lexer {
    public Token getNextToken() throws  Exception;
    public void readFile(String filename) throws  Exception;
}
