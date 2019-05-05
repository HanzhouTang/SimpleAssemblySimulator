package Instructions;

import common.BitSetUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import static Instructions.OpCode.*;

public class InstructionTest {
    private static final Logger LOGGER = Logger.getLogger(InstructionTest.class);

    @Test
    public void AddTwo8BitsInstruction_13() throws Exception {
        //add cl, al
        Operand cl = new Operand.Builder().register(Register.fromName("cl").get()).build();
        Operand al = new Operand.Builder().register(Register.fromName("al").get()).build();
        Instruction add = new Instruction(ADD, cl, al);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2, bytes.length);
        Assert.assertEquals("0000000011000001", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_13() throws Exception {
        //add cl,al
        final String binaryCode = "0000000011000001";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(2, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo32BitsInstruction_14() throws Exception {
        Operand ecx = new Operand.Builder().register(Register.fromName("ecx").get()).build();
        Operand eax = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Instruction add = new Instruction(ADD, ecx, eax);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2, bytes.length);
        Assert.assertEquals("0000000111000001", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_14() throws Exception {
        //add ecx,eax
        final String binaryCode = "0000000111000001";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(2, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }


    @Test
    public void AddTwo32BitsInstruction_15() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("edx").get()).build();
        Operand source = new Operand.Builder().displacement(-128).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        LOGGER.info(builder.toString());
        Assert.assertEquals(6, bytes.length);
        Assert.assertEquals("000000110001010111111111111111111111111110000000", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_15() throws Exception {
        // add edx, [-128]
        final String binaryCode = "000000110001010111111111111111111111111110000000";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(6, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }


    @Test
    public void AddTwo32BitsInstruction_16() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("edi").get()).build();
        Operand source = new Operand.Builder().base(Register.fromName("ebx").get()).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2, bytes.length);
        Assert.assertEquals("0000001100111011", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_16() throws Exception {
        //add edi, [ebx]
        final String binaryCode = "0000001100111011";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(2, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo32BitsInstruction_17() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Operand source = new Operand.Builder().base(Register.fromName("esi").get()).displacement(-1).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals("000000110100011011111111", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_17() throws Exception {
        //add eax,[esi-1]
        final String binaryCode = "000000110100011011111111";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(3, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo32BitsInstruction_18() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("ebx").get()).build();
        Operand source = new Operand.Builder().base(Register.fromName("ebp").get()).displacement(-129).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(6, bytes.length);
        // Here is some problem.
        // Need fix it.
        Assert.assertEquals("000000111001110111111111111111111111111101111111", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_18() throws Exception {
        //add ebx, [ebp-129]
        final String binaryCode = "000000111001110111111111111111111111111101111111";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(6, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    // need add one more test for 4 byte displacement

    @Test
    public void AddTwo32BitsInstruction_19() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("ebp").get()).build();
        Operand source = new Operand.Builder().sib(null, Register.fromName("eax").get(), 1).displacement(-128).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(4, bytes.length);
        Assert.assertEquals("00000011011011000000010110000000", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_19() throws Exception {
        //add ebp, [eax*1-128]
        final String binaryCode = "00000011011011000000010110000000";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(4, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo32BitsInstruction_20() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("ecx").get()).build();
        Operand source = new Operand.Builder().sib(Register.fromName("ebx").get(), Register.fromName("edi").get(), 4).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals("000000110000110010111011", builder.toString());
    }


    @Test
    public void CreateInstructionByByteArray_20() throws Exception {
        //add ecx, [ebx+edi*4]
        final String binaryCode = "000000110000110010111011";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(3, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo32BitsInstruction_immediate() throws Exception {
        Operand dest = new Operand.Builder().register(Register.fromName("ebp").get()).build();
        Operand source = new Operand.Builder().immediate(-128).build();
        Instruction add = new Instruction(ADD, dest, source);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(6, bytes.length);
        //11111111111111111111111110000000
        Assert.assertEquals("100000011100010111111111111111111111111110000000", builder.toString());
    }

    // something going wrong here. Need fix it.
    @Test
    public void CreateInstructionByByteArray_immediate() throws Exception {
        //add ebp, -128
        final String binaryCode = "100000011100010111111111111111111111111110000000";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(6, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void AddTwo16BitsInstruction() throws Exception {
        Operand cl = new Operand.Builder().register(Register.fromName("cx").get()).build();
        Operand al = new Operand.Builder().register(Register.fromName("ax").get()).build();
        Instruction add = new Instruction(ADD, cl, al);
        byte[] bytes = add.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(3, bytes.length);
        Assert.assertEquals("111111110000000111000001", builder.toString());
    }

    @Test
    public void JumpTest() throws Exception {
        Operand source = new Operand.Builder().immediate(123).build();
        Instruction instruction = new Instruction(JMP, null, source);
        byte[] bytes = instruction.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(6, bytes.length);
        Assert.assertEquals("110101010000000000000000000000000000000001111011", builder.toString());
    }

    @Test
    public void RetTest() throws Exception {
        Instruction instruction = new Instruction(RET, null, null);
        byte[] bytes = instruction.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2, bytes.length);
        Assert.assertEquals("0110010100000000", builder.toString());
    }


    // for one operand instruction, always set source
    @Test
    public void CreateInstructionByByteArray_jump() throws Exception {
        //jump 123
        final String binaryCode = "110101010000000000000000000000000000000001111011";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(6, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void PushTest() throws Exception {
        Operand source = new Operand.Builder().register(Register.fromName("eax").get()).build();
        Instruction instruction = new Instruction(PUSH, null, source);
        byte[] bytes = instruction.toBytes();
        StringBuilder builder = new StringBuilder();
        for (byte x : bytes) {
            builder.append(Integer.toBinaryString((x & 0xFF) + 0x100).substring(1));
        }
        Assert.assertEquals(2, bytes.length);
        Assert.assertEquals("0001110100000000", builder.toString());
    }

    @Test
    public void CreateInstructionByByteArray_push() throws Exception {
        //push eax
        final String binaryCode = "0001110100000000";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(2, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

    @Test
    public void CreateInstructionByByteArray_16register() throws Exception {
        //add cx,ax
        final String binaryCode = "111111110000000111000001";
        byte[] bytes = BitSetUtils.fromBinaryStringToByteArray(binaryCode);
        MutableInt currentLocation = new MutableInt(0);
        Instruction instruction = Instruction.fromBytes(bytes, currentLocation);
        Assert.assertEquals(3, currentLocation.getValue().intValue());
        Assert.assertEquals(binaryCode, BitSetUtils.toString(instruction.toBytes()));
    }

}
