import config.VirtualMachineProperties;
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
import virtualmachine.ClockCycleCounter;
import virtualmachine.Memory;
import virtualmachine.RegisterManager;
import virtualmachine.VirtualMachine;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@EnableAutoConfiguration
@SpringBootTest(classes = {FreeMarkerConfig.class, Render.class, VirtualMachine.class,
        ClockCycleCounter.class, Memory.class, ReservedTable.class,
        RegisterManager.class, VirtualMachineProperties.class,
        ReservationStation.class, ReorderBuffer.class, ReservedTable.class})
public class RenderTest {
    @Autowired
    VirtualMachine virtualMachine;
    @Autowired
    Render render;
    @Autowired
    RegisterManager registerManager;
    @Autowired
    ReservationStation reservationStation;
    @Autowired
    ReorderBuffer reorderBuffer;

    @Autowired
    ReservedTable reservedTable;
    @Test
    public void RegisterRenderTest() throws Exception {
        render.registerToHtml(registerManager, "registerStatus");
    }

    @Test
    public void ReservationStationRenderTest() throws Exception {
        render.ReservationStationToHtml(reservationStation, "reservation station");
    }

    @Test
    public void ReorderBufferRenderTest() throws Exception {
        render.ReorderBufferToHtml(reorderBuffer, "reorder buffer");
    }

    @Test
    public void VirtualMachineTest() throws Exception {
        render.virtualMachineToHtml(virtualMachine, "virtual machine");
    }

    @Test
    public void ReservedTableTest() throws Exception {
        render.ReservedTableToHtml(reservedTable, "reserved table");
    }
}
