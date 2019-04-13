package assembler;


import java.util.List;

/**
 * The interface of lexer.
 * Lexer must be bind to a file by calling readFile method.
 * Basically, a lexer should be able to
 * <ul>
 *     <li>Get next token.</li>
 *     <li>Look ahead next k tokens and not affecting current states.</li>
 *     <li>Get current lexer status.</li>
 * </ul>
 */
public interface Lexer {
    public Token getNextToken();
    public void readFile(String filename) throws  Exception;
    public List<LexemeTokenWrapper> lookAheadK(int k);
    public Status getStatus();
}
