package assembler;

import Instructions.Instruction;
import OutputFile.ObjFile;
import assembler.AssemblerConfig;
import assembler.SimpleLexer;
import assembler.SimpleParser;
import common.BitSetUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AssemblerConfig.class)
public class ParserTest {
    @Autowired
    SimpleParser simpleParser;

    @Test
    public void intiParserTest() throws Exception {
        Assert.assertNotNull(simpleParser.getLexer());
    }

    @Test
    public void dataSegmentTest() throws Exception {
        URI uri = getClass().getClassLoader().getResource("data_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(64, obj.getDataSegment().getCurrentLocation());
    }

    @Test
    public void dataSegmentTest1() throws Exception {
        URI uri = getClass().getClassLoader().getResource("dataStr_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(11, obj.getDataSegment().getCurrentLocation());
        int hello = obj.getDataSegment().getLocationByName("Hello");
        Assert.assertEquals(0, hello);
        int world = obj.getDataSegment().getLocationByName("World");
        Assert.assertEquals(5, world);
        Byte o = obj.getDataSegment().get(4);
        Byte expected = 111;
        Assert.assertEquals(expected, o);
    }

    @Test
    public void dataSegmentDupTest() throws Exception {
        URI uri = getClass().getClassLoader().getResource("dataDup_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(155, obj.getDataSegment().getCurrentLocation());
        int hello = obj.getDataSegment().getLocationByName("Hello");
        Assert.assertEquals(0, hello);
        int world = obj.getDataSegment().getLocationByName("World");
        Assert.assertEquals(5, world);
        Byte symbol = obj.getDataSegment().get(16);
        Byte expected = 33;
        Assert.assertEquals(expected, symbol);
        Byte w = 119;
        Assert.assertEquals(w, obj.getDataSegment().get(65));
        Assert.assertEquals(symbol, obj.getDataSegment().get(73));
        Assert.assertEquals(symbol, obj.getDataSegment().get(82));
    }

    @Test
    public void dataSegmentExprTest() throws Exception {
        URI uri = getClass().getClassLoader().getResource("dataExpr_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(4, obj.getDataSegment().getCurrentLocation());
        Byte twelve = 12;
        Byte thirteen = 13;
        Byte fourteen = 14;
        Byte negSeventeen = -17;
        Byte negTwelve = -12;
        Byte negTwentyOne = -21;
        Assert.assertEquals(twelve, obj.getDataSegment().get(0));
        Assert.assertEquals(thirteen, obj.getDataSegment().get(1));
        Assert.assertEquals(fourteen, obj.getDataSegment().get(2));
        Assert.assertEquals(negTwentyOne, obj.getDataSegment().get(3));
    }

    @Test
    public void dataSegmentExprTest1() throws Exception {
        URI uri = getClass().getClassLoader().getResource("dataExpr1_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(10, obj.getDataSegment().getCurrentLocation());
        Byte eight = 8;
        Byte zero = 0;
        Assert.assertEquals(eight, obj.getDataSegment().get(8));
        Assert.assertEquals(zero, obj.getDataSegment().get(9));
    }

    @Test
    public void twoDataSegmentExprTest() throws Exception {

        URI uri = getClass().getClassLoader().getResource("dataTwoSegments_.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(160, obj.getDataSegment().getCurrentLocation());
        Byte five = 5;
        Byte w = 119;
        Byte d = 100;
        Assert.assertEquals(five, obj.getDataSegment().get(4));
        Assert.assertEquals(w, obj.getDataSegment().get(10));
        Assert.assertEquals(w, obj.getDataSegment().get(16));
        Assert.assertEquals(w, obj.getDataSegment().get(64));
        Assert.assertEquals(d, obj.getDataSegment().get(155));
    }


    @Test
    public void instructionTest() throws Exception {
        URI uri = getClass().getClassLoader().getResource("ins.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(10, obj.getCodeSegment().getEntryPoint());
        Assert.assertEquals(10, obj.getCodeSegment().getProcedureEntryPoint("add_"));
        List<Byte> instructions = obj.getCodeSegment().getCode();
        byte[] bytes = BitSetUtils.toByteArray(instructions);
        MutableInt currentLocation = new MutableInt(0);
        List<Instruction> ins = new ArrayList<>();
        String instructionCode = BitSetUtils.toString(bytes);
        StringBuilder builder = new StringBuilder();
        while (currentLocation.getValue() < bytes.length) {
            Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
            builder.append(BitSetUtils.toString(instruction.toBytes()));
        }
        Assert.assertEquals(instructionCode, builder.toString());
    }

    @Test
    public void instructionTest1() throws Exception {
        URI uri = getClass().getClassLoader().getResource("ins_1.asm").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        ObjFile obj = simpleParser.parse(filename);
        Assert.assertEquals(10, obj.getCodeSegment().getEntryPoint());
        Assert.assertEquals(10, obj.getCodeSegment().getProcedureEntryPoint("add_"));
        List<Byte> instructions = obj.getCodeSegment().getCode();
        byte[] bytes = BitSetUtils.toByteArray(instructions);
        MutableInt currentLocation = new MutableInt(0);
        List<Instruction> ins = new ArrayList<>();
        String instructionCode = BitSetUtils.toString(bytes);
        StringBuilder builder = new StringBuilder();
        List<Instruction> instructionList = new ArrayList<>();
        while (currentLocation.getValue() < bytes.length) {
            Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
            instructionList.add(instruction);
            builder.append(BitSetUtils.toString(instruction.toBytes()));
        }
        Assert.assertEquals(instructionCode, builder.toString());
        Assert.assertEquals(12,instructionList.size());
    }
}
