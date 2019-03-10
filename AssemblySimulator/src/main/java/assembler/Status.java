package assembler;

import java.util.Iterator;
import java.util.Map;

public class Status {
    Iterator<Map.Entry<Integer,String>> iterator;
    Integer lineIndex = 0;
    Integer charIndex = 0;
    Token currentToken = Token.Invalid;
    String currentLexeme = "";
    String currentLine = "";

    Status(Iterator <Map.Entry<Integer,String>> iterator){
        this.iterator = iterator;
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

    public Iterator<Map.Entry<Integer, String>> getIterator() {
        return iterator;
    }

    public void setIterator(Iterator<Map.Entry<Integer, String>> iterator) {
        this.iterator = iterator;
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

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }
}

