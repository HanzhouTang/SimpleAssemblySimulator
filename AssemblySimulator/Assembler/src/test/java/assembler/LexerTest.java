package assembler;

import assembler.SimpleLexer;
import assembler.Token;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LexerTest {

    private static final Logger LOGGER = Logger.getLogger(LexerTest.class);
    @Test
    public void readFileTest() throws Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        Assert.assertNotNull(lexer.getLines());
    }

    @Test
    public void StripCommentsTest() throws Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        Assert.assertNotNull(lexer.getLines());
        Assert.assertNotEquals(0,lexer.getLines().size());
        Assert.assertEquals(".model flat,c\n",lexer.getLines().get(0).getCode());
    }
    @Test
    public void getTokensTest() throws  Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
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
        }
        LOGGER.info("all tokens for add_.asm "+tokens);
        Assert.assertNotEquals(0,tokens.size());
    }

    @Test
    public void lookAheadKTest() throws Exception{
        URI uri = getClass().getClassLoader().getResource("add_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        SimpleLexer lexer = new SimpleLexer();
        lexer.readFile(filename);
        List<Token> tokens = lexer.lookAheadK(10).stream().map(LexemeTokenWrapper::getToken).collect(Collectors.toList());
        LOGGER.info("tokens " + tokens);
        Assert.assertEquals(Arrays.asList(Token.DotString, Token.String, Token.Comma,
                Token.String, Token.NewLine, Token.DotString, Token.NewLine,
                Token.String, Token.String, Token.NewLine),tokens);
        Token token = lexer.getNextToken();
        Assert.assertEquals(Token.DotString,token);
        Assert.assertEquals(".model",lexer.getStatus().getCurrentLexeme());
    }

    @Test
    public void lookAheadKTest1() throws Exception{
        URI uri = getClass().getClassLoader().getResource("data1_.asm").toURI();
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
        }
        LOGGER.info("all tokens for data1_.asm "+tokens);
        lexer.getNextToken();
        lexer.getNextToken();
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        LOGGER.info("the wrapper is "+ wrapper.getToken()+" lexeme "+wrapper.getLexeme());
        Assert.assertEquals(Token.EndofContent,wrapper.getToken());


    }
}
