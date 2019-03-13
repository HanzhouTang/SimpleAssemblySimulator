package assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleParser {
    @Autowired
    private Lexer lexer;

    public Lexer getLexer(){
        return lexer;
    }

}
