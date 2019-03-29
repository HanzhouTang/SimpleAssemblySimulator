import assembler.SimpleLexer;
import assembler.Token;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class LexerTest {



    @Test
    public void readFile() throws Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        Assert.assertNotNull(lexer.getLines());
    }

    @Test
    public void StripComments() throws Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        Assert.assertNotNull(lexer.getLines());
        Assert.assertNotEquals(0,lexer.getLines().size());
        Assert.assertEquals(".model flat,c\n",lexer.getLines().get(2));
    }
    @Test
    public void getTokens() throws  Exception{
        URI uri = getClass().getClassLoader().getResource("MemoryAddressing_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        Assert.assertNotNull(lexer.getLines());
        Assert.assertNotEquals(0,lexer.getLines().size());
        Token token = Token.Invalid;
        List<String> tokens = new ArrayList<>();
        while(token!=Token.EndofContent){
            token = lexer.getNextToken();
            tokens.add(token.name());
            System.out.println("lexeme: "+lexer.getStatus().getCurrentLexeme()+" token: "+token.name());
        }
        Assert.assertNotEquals(0,tokens.size());
    }
}
