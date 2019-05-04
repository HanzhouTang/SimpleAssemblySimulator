package OutputFile;

import Instructions.Instruction;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Code segment.
 * To be honest, I can add an abstract class for code segment and data segment.
 * For now, not.
 */
public class CodeSegment implements SupportTwoParsingPass {
    private static final Logger LOGGER = Logger.getLogger(CodeSegment.class);
    List<Byte> code = new ArrayList<>();
    Map<String, Integer> labelTable = new HashMap<>();
    Map<String, Procedure> procedureTable = new HashMap<>();
    private int entryPoint;
    private int baseAddress = 0;

    public int getEntryPoint() {
        return entryPoint;
    }

    public List<Byte> getCode(){
        return code;
    }
    public void setEntryPoint(String entryPoint) throws Exception {
        if (procedureTable.containsKey(entryPoint)) {
            int result = procedureTable.get(entryPoint).getStart() + baseAddress;
            LOGGER.debug("relocate location " + procedureTable.get(entryPoint).getStart() + " by baseAddress " + baseAddress + " result is " + +result);
            this.entryPoint = result;
        } else {
            throw new Exception("the entry point " + entryPoint + " is not existed");
        }
    }

    public int getProcedureEntryPoint(String procedure) {
        int location = -1;
        if (procedureTable.containsKey(procedure)) {
            location = procedureTable.get(procedure).getStart();
        }
        if (location != -1) {
            LOGGER.debug("relocate location " + location + " by baseAddress " + baseAddress + " result is " + location + baseAddress);
            return location + baseAddress;
        }
        return -1;
    }

    public void addLabel(String name) {
        int currentLocation = code.size();
        labelTable.put(name, currentLocation);
    }

    public void addInstruction(Instruction instruction) throws Exception {
        LOGGER.debug("add instruction {" + instruction + " }");
        byte[] bytes = instruction.toBytes();
        if (bytes != null) {
            for (byte b : bytes) {
                code.add(b);
            }
        }
    }

    public void addProcedure(Procedure p) throws Exception {
        String name = p.getName();
        if (procedureTable.containsKey(name)) {
            throw new Exception("the procedure " + name + " has been defined before");
        }
        procedureTable.put(name, p);
    }

    public Byte get(int index) {
        if (index < code.size()) {
            return code.get(index);
        } else {
            return null;
        }
    }

    public int getCurrentLocation() {
        return code.size();
    }

    public int getLocationByLabel(String label) {
        int location = labelTable.getOrDefault(label, -1);
        if (location != -1) {
            LOGGER.debug("relocate location " + location + " by baseAddress " + baseAddress + " result is " + location + baseAddress);
            return location + baseAddress;
        }
        return -1;
    }

    @Override
    public void resetAfterFirstParsingPass(Object... param) throws Exception {
        if (param.length == 0) {
            throw new Exception("must given length of data segment");
        }
        baseAddress = (int) param[0];
        code.clear();
    }
}
