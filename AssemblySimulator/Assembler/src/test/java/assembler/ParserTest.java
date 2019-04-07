package assembler;

import OutputFile.ObjFile;
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
    @Test
    public void dataSegmentTest() throws Exception{
        URI uri = getClass().getClassLoader().getResource("data_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(64,obj.getDataSegment().getCurrentLocation());
    }
    @Test
    public void dataSegmentTest1() throws Exception{
        URI uri = getClass().getClassLoader().getResource("dataStr_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(11,obj.getDataSegment().getCurrentLocation());
        int hello = obj.getDataSegment().getLocationByName("Hello");
        Assert.assertEquals(0,hello);
        int world = obj.getDataSegment().getLocationByName("World");
        Assert.assertEquals(5,world);
        Byte o = obj.getDataSegment().get(4);
        Byte expected = 111;
        Assert.assertEquals(expected,o);
    }
    @Test
    public void dataSegmentDupTest() throws Exception{
        URI uri = getClass().getClassLoader().getResource("dataDup_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(155,obj.getDataSegment().getCurrentLocation());
        int hello = obj.getDataSegment().getLocationByName("Hello");
        Assert.assertEquals(0,hello);
        int world = obj.getDataSegment().getLocationByName("World");
        Assert.assertEquals(5,world);
        Byte symbol = obj.getDataSegment().get(16);
        Byte expected = 33;
        Assert.assertEquals(expected,symbol);
        Byte w = 119;
        Assert.assertEquals(w,obj.getDataSegment().get(65));
        Assert.assertEquals(symbol,obj.getDataSegment().get(73));
        Assert.assertEquals(symbol,obj.getDataSegment().get(82));
    }
}
