import Instructions.Instruction;
import Instructions.OpCode;
import config.VirtualMachineProperties;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import render.FreeMarkerConfig;
import render.Render;
import tomasulo.ReorderBuffer;
import tomasulo.ReservationStation;
import tomasulo.ReservedTable;
import virtualmachine.*;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Queue;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@EnableAutoConfiguration
@SpringBootTest(classes = {ClockCycleCounter.class, Memory.class,
        RegisterManager.class, VirtualMachine.class, VirtualMachineProperties.class,
        ReorderBuffer.class, ReservationStation.class, ReservedTable.class, Render.class, FreeMarkerConfig.class})
public class virtualMachineTest {

    private static Logger LOGGER = Logger.getLogger(virtualMachineTest.class);
    @Autowired
    VirtualMachine virtualMachine;

    @Autowired
    VirtualMachineProperties virtualMachineProperties;

    @Test
    public void getNextInstructionTest() throws Exception {
        URI uri = getClass().getClassLoader().getResource("nop.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.loadObjFile(filename);
        Instruction ins = virtualMachine.getNextInstruction();
        Assert.assertEquals(OpCode.NOP, ins.getOpcode());
    }

    @Test
    public void propertiesSettingTest() {
        Assert.assertNotNull(virtualMachineProperties);
        Assert.assertEquals(3, virtualMachineProperties.getLoadMemoryNeededCycle().intValue());
        Assert.assertEquals(3, virtualMachineProperties.getSaveMemoryNeededCycle().intValue());
        Assert.assertEquals(1, virtualMachineProperties.getExecutionTime(OpCode.NOP).intValue());
    }


    @Test
    public void runNopInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("nop.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(3, q.size());
    }

    @Test
    public void run2NopInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("nop2.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
    }

    @Test
    public void runAddInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("add_.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
        int value = virtualMachine.getRegisterManager().getRegister("eax").getContent();
        Assert.assertEquals(26, value);
    }

    @Test
    public void runSubInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("sub_.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
        int value = virtualMachine.getRegisterManager().getRegister("eax").getContent();
        Assert.assertEquals(0, value);
    }


    @Test
    public void runSub1Instruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("sub1.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
        int value = virtualMachine.getRegisterManager().getRegister("eax").getContent();
        Assert.assertEquals(0, value);
    }

    @Test
    public void runMulInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("mul_.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
        int value = virtualMachine.getRegisterManager().getRegister("eax").getContent();
        Assert.assertEquals(169, value);
    }

    @Test
    public void runDivInstruction() throws Exception {
        URI uri = getClass().getClassLoader().getResource("test_.obj").toURI();
        Assert.assertNotNull(uri);
        String filename = Paths.get(uri).toString();
        virtualMachine.reset();
        virtualMachine.loadObjFile(filename);
        virtualMachine.run();
        Queue<Message> q = virtualMachine.getEventRecorder();
        //Assert.assertEquals(6, q.size());
        //int value = virtualMachine.getRegisterManager().getRegister("eax").getContent();
        //Assert.assertEquals(169, value);
    }
}
