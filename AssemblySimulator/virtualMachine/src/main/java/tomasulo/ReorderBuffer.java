package tomasulo;

import config.VirtualMachineProperties;
import instructions.InstructionBase;
import instructions.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReorderBuffer {
    private static Logger LOGGER = Logger.getLogger(ReorderBuffer.class);

    enum ReorderBufferState {EXEC, WAIT_EXEC, ISSUE, COMMIT}


    public static class ReorderBufferEntry {
        final InstructionBase executedInstruction;
        boolean isBusy = false;
        final Result result;
        final ReorderBufferState state;

        public ReorderBufferEntry(InstructionBase ins, boolean b, Result r, ReorderBufferState s) {
            executedInstruction = ins;
            isBusy = b;
            result = r;
            state = s;
        }
    }

    private final ReorderBufferEntry buffer[];
    private int size = 0;
    private int head = 0;

    @Autowired
    public ReorderBuffer(VirtualMachineProperties properties) {
        buffer = new ReorderBufferEntry[properties.getReorderBufferSize()];
    }

}
