package Instructions;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import static Instructions.OpCode.*;
public class InstructionTest {
    private static final Logger LOGGER = Logger.getLogger(InstructionTest.class);
    @Test
    public  void AddTwo8BitsInstruction_13() throws Exception{
        Operand cl = new Operand.Builder().register(Register.fromName("cl").get()).build();
        Operand al = new Operand.Builder().register(Register.fromName("al").get()).build();
        Instruction add = new Instruction(ADD,cl,al);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2,bytes.length);
        Assert.assertEquals("0000000011000001",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_14() throws Exception{
        Operand ecx = new Operand.Builder().register(Register.fromName("ecx").get()).build();
        Operand eax = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Instruction add = new Instruction(ADD,ecx,eax);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2,bytes.length);
        Assert.assertEquals("0000000111000001",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_15() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("edx").get()).build();
        Operand source = new Operand.Builder().displacement(-128).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        LOGGER.info(builder.toString());
        Assert.assertEquals(6,bytes.length);
        Assert.assertEquals("000000110001010111111111111111111111111110000000",builder.toString());
    }


    @Test
    public  void AddTwo32BitsInstruction_16() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("edi").get()).build();
        Operand source = new Operand.Builder().indirect(Register.fromName("ebx").get()).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2,bytes.length);
        Assert.assertEquals("0000001100111011",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_17() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Operand source = new Operand.Builder().indirect(Register.fromName("esi").get()).displacement(0).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3,bytes.length);
        Assert.assertEquals("000000110100011000000000",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_18() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Operand source = new Operand.Builder().indirect(Register.fromName("esi").get()).displacement(0).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3,bytes.length);
        Assert.assertEquals("000000110100011000000000",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_19() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("ebp").get()).build();
        Operand source = new Operand.Builder().sib(null,Register.fromName("eax").get(),1).displacement(-128).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(7,bytes.length);
        Assert.assertEquals("00000011001011000000010111111111111111111111111110000000",builder.toString());
    }

    @Test
    public  void AddTwo32BitsInstruction_20() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("ecx").get()).build();
        Operand source = new Operand.Builder().sib(Register.fromName("ebx").get(),Register.fromName("edi").get(),4).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3,bytes.length);
        Assert.assertEquals("000000110000110010111011",builder.toString());
    }


    @Test
    public  void AddTwo32BitsInstruction_immediate() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("ebp").get()).build();
        Operand source = new Operand.Builder().immediate(-128).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(6,bytes.length);
        //11111111111111111111111110000000
        Assert.assertEquals("100000011100010111111111111111111111111110000000",builder.toString());
    }

    @Test
    public  void AddTwo16BitsInstruction() throws Exception{
        Operand cl = new Operand.Builder().register(Register.fromName("cx").get()).build();
        Operand al = new Operand.Builder().register(Register.fromName("ax").get()).build();
        Instruction add = new Instruction(ADD,cl,al);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3,bytes.length);
        Assert.assertEquals("111111110000000111000001",builder.toString());
    }
}
