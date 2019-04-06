package assembler;


import java.util.List;

public interface Lexer {
    public Token getNextToken();
    public void readFile(String filename) throws  Exception;
    public List<Token> lookAheadK(int k);
}
