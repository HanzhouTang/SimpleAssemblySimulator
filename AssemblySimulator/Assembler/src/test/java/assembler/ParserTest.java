package assembler;

import assembler.AssemblerConfig;
import assembler.SimpleLexer;
import assembler.SimpleParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.nio.file.Paths;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AssemblerConfig.class)
public class ParserTest {
    @Autowired
    SimpleParser simpleParser;
    @Test
    public void intiParserTest() throws Exception{
        Assert.assertNotNull(simpleParser.getLexer());
    }
}
