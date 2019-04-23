package assembler;

/**
 * The status of lexer.
 * Basically, the status of lexer should be
 * <ul>
 *     <li>a pointer to current line</li>
 *     <li>a pointer to current character</li>
 *     <li>current line number in source code</li>
 *     <li>current token</li>
 *     <li>current lexeme</li>
 *     <li>codes of current line</li>
 * </ul>
 */
public class Status {
    Integer iterator = 0;
    Integer lineIndex = 0;
    Integer charIndex = 0;
    Token currentToken = Token.Invalid;
    String currentLexeme = "";
    String codeOfCurrentLine = "";

    Status(){
    }
    Status(Status status){
        this.iterator = status.getIterator();
        this.lineIndex = status.getLineIndex();
        this.charIndex = status.getCharIndex();
        this.currentLexeme = status.getCurrentLexeme();
        this.currentToken = status.getCurrentToken();
        this.codeOfCurrentLine = status.getCodeOfCurrentLine();
    }

    public Integer getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(Integer lineIndex) {
        this.lineIndex = lineIndex;
    }

    public Integer getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(Integer charIndex) {
        this.charIndex = charIndex;
    }

    public Integer getIterator() {
        return iterator;
    }

    public void setIterator(Integer i) {
        this.iterator = i;
    }

    public String getCurrentLexeme() {
        return currentLexeme;
    }

    public void setCurrentLexeme(String currentLexeme) {
        this.currentLexeme = currentLexeme;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(Token currentToken) {
        this.currentToken = currentToken;
    }

    public String getCodeOfCurrentLine() {
        return codeOfCurrentLine;
    }

    public void setCodeOfCurrentLine(String codeOfCurrentLine) {
        this.codeOfCurrentLine = codeOfCurrentLine;
    }
}

