package render;

import Instructions.Register;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tomasulo.ReorderBuffer;
import tomasulo.ReservationStation;
import tomasulo.ReservedTable;
import virtualmachine.Message;
import virtualmachine.PhysicalRegister;
import virtualmachine.RegisterManager;
import virtualmachine.VirtualMachine;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Render {
    @Autowired
    FreeMarkerConfig freeMarkerConfig;

    public static class RegisterWrapper {
        String name;
        int value;

        public RegisterWrapper(Map.Entry<String, PhysicalRegister> entry) {
            this.name = entry.getKey();
            this.value = entry.getValue().getContent();
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }


    public static class ReorderBufferEntryWrapper {
        String busy;
        String result;
        String reorderBufferState;
        String reservationIndex;
        String address;
        int index;

        public int getIndex() {
            return index;
        }

        public String getAddress() {
            return address;
        }

        public String getBusy() {
            return busy;
        }

        public String getReorderBufferState() {
            return reorderBufferState;
        }

        public String getReservationIndex() {
            return reservationIndex;
        }

        public String getResult() {
            return result;
        }

        public ReorderBufferEntryWrapper(ReorderBuffer.ReorderBufferEntry entry, int i) {
            if (entry != null) {
                busy = entry.isBusy ? "True" : "False";
                if (entry.result != null) {
                    result = entry.result.getResult() == null ? "N/A" : entry.result.getResult().toString();
                } else {
                    result = "N/A";
                }
                reorderBufferState = entry.state == null ? "N/A" : entry.state.toString();
                reservationIndex = "#" + entry.reservationIndex;
                address = entry.dest == null ? "N/A" : entry.dest.toString();
            } else {
                busy = "False";
                result = "N/A";
                reorderBufferState = "N/A";
                reservationIndex = "N/A";
                address = "N/A";
            }
            index = i;
        }


    }

    public static class ReservationStationEntryWrapper {
        String busy;
        int index;
        String instruction;
        String vj;
        String qj;

        public ReservationStationEntryWrapper(ReservationStation.ReservationStationEntry entry, int i) {
            index = i;
            if (entry != null) {
                instruction = entry.getInstruction() == null ? "N/A" : entry.getInstruction().getInstruction().toString();
                vj = entry.getVj() == null ? "N/A" : entry.getVj().toString();
                qj = entry.getQj() == null ? "N/A" : "#" + entry.getQj().toString();
                busy = entry.isBusy() ? "True" : "False";
            } else {
                busy = "False";
                instruction = "N/A";
                vj = "N/A";
                qj = "N/A";
            }
        }

        public String getBusy() {
            return busy;
        }

        public int getIndex() {
            return index;
        }

        public String getInstruction() {
            return instruction;
        }

        public String getQj() {
            return qj;
        }

        public String getVj() {
            return vj;
        }
    }

    public void registerToHtml(RegisterManager registerManager, String title) throws Exception {
        Template template = freeMarkerConfig.getCfg().getTemplate("register.html");
        //Writer consoleWriter = new FileWriter("");//new OutputStreamWriter(System.out);
        List<RegisterWrapper> list = registerManager.toList(RegisterWrapper::new);
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("registers", list);
        input.put("title", title);
        Writer writer = freeMarkerConfig.getOutputWriter(title + ".html");
        template.process(input, writer);
        writer.flush();
    }

    public void ReservationStationToHtml(ReservationStation reservationStation, String title) throws Exception {
        Template template = freeMarkerConfig.getCfg().getTemplate("reservationStationTable.html");
        List<ReservationStationEntryWrapper> list = reservationStation.toList();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("reservationStationTable", list);
        input.put("title", title);
        Writer writer = freeMarkerConfig.getOutputWriter(title + ".html");
        template.process(input, writer);
        writer.flush();
    }

    public void ReorderBufferToHtml(ReorderBuffer reorderBuffer, String title) throws Exception {
        Template template = freeMarkerConfig.getCfg().getTemplate("reorderBufferTable.html");
        List<ReorderBufferEntryWrapper> list = reorderBuffer.toList();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("reorderBufferTable", list);
        input.put("title", title);
        Writer writer = freeMarkerConfig.getOutputWriter(title + ".html");
        template.process(input, writer);
        writer.flush();
    }

    public void ReservedTableToHtml(ReservedTable reservedTable, String title) throws Exception {
        Template template = freeMarkerConfig.getCfg().getTemplate("reservedTable.html");
        Set<Map.Entry<Integer, Integer>> memEntries = reservedTable.memoryToSet();
        Set<Map.Entry<Register, Integer>> registerEntries = reservedTable.registerToSet();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("registerReservedTable", registerEntries);
        input.put("memoryReservedTable", memEntries);
        input.put("title", title);
        Writer writer = freeMarkerConfig.getOutputWriter(title + ".html");
        template.process(input, writer);
        writer.flush();
    }

    public void virtualMachineToHtml(VirtualMachine virtualMachine, String title) throws Exception {
        Template template = freeMarkerConfig.getCfg().getTemplate("virtualMachine.html");
        List<ReorderBufferEntryWrapper> reorderBufferList = virtualMachine.getReorderBuffer().toList();
        List<ReservationStationEntryWrapper> reservationStationList = virtualMachine.getReservationStation().toList();
        List<RegisterWrapper> registerList = virtualMachine.getRegisterManager().toList(RegisterWrapper::new);
        Set<Map.Entry<Integer, Integer>> memEntries = virtualMachine.getReservedTable().memoryToSet();
        Set<Map.Entry<Register, Integer>> registerEntries = virtualMachine.getReservedTable().registerToSet();
        Map<String, Object> input = new HashMap<String, Object>();
        List<Message> msgs = virtualMachine.getMessages();
        int clockCycle = virtualMachine.getClockCycleCounter().getCurrentClockCycle();

        virtualMachine.resetMessageQueue();
        input.put("msgs", msgs);
        input.put("reorderBufferTable", reorderBufferList);
        input.put("reservationStationTable", reservationStationList);
        input.put("registers", registerList);
        input.put("registerReservedTable", registerEntries);
        input.put("memoryReservedTable", memEntries);
        input.put("title", title);
        int previousClockCycle = Math.max(clockCycle - 1, 0);
        input.put("previousUrl", title + "-" + previousClockCycle + ".html");
        input.put("nextUrl", title + "-" + (clockCycle + 1) + ".html");
        Writer writer = freeMarkerConfig.getOutputWriter(title + "-" + clockCycle + ".html");
        template.process(input, writer);
        writer.flush();
    }
}
