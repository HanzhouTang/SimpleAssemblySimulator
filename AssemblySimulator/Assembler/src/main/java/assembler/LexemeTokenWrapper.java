package assembler;

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
