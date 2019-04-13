package assembler;

/**
 * A wrapper to combine token and lexeme together.
 */
public class LexemeTokenWrapper {
    final Token token;
    final String lexeme;
    LexemeTokenWrapper(Token t, String s){
        token = t;
        lexeme = s;
    }
    Token getToken(){
        return token;
    }
    String getLexeme(){
        return lexeme;
    }
}
