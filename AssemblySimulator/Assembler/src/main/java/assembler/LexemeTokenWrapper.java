package assembler;

/**
 * A wrapper to combine token and lexeme together.
 */
public class LexemeTokenWrapper {
    final Token token;
    final String lexeme;
    final int lineIndex;

    LexemeTokenWrapper(Token t, String s, int index) {
        token = t;
        lexeme = s;
        lineIndex = index;
    }

    Token getToken() {
        return token;
    }

    String getLexeme() {
        return lexeme;
    }

    int getLineIndex() {
        return lineIndex;
    }
}
