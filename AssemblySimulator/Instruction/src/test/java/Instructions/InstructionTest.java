package Instructions;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import static Instructions.OpCode.*;
public class InstructionTest {
    private static final Logger LOGGER = Logger.getLogger(InstructionTest.class);
    @Test
    public  void AddTwo8BitsInstruction() throws Exception{
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
    public  void AddTwo32BitsInstruction() throws Exception{
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
    public  void AddTwo32BitsInstruction3() throws Exception{
        Operand dest = new Operand.Builder().register(Register.fromName("edx").get()).build();
        Operand source = new Operand.Builder().displacement(0).build();
        Instruction add = new Instruction(ADD,dest,source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for(byte x : bytes){
            builder.append(Integer.toBinaryString((x& 0xFF) + 0x100).substring(1));
        }
        LOGGER.info(builder.toString());
        Assert.assertEquals(6,bytes.length);
        Assert.assertEquals("000000110001110100000000000000000000000000000000",builder.toString());
    }


    @Test
    public  void AddTwo32BitsInstruction1() throws Exception{
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
    public  void AddTwo32BitsInstruction2() throws Exception{
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
}
