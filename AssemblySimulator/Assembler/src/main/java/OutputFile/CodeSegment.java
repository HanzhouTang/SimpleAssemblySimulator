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
    Map<String, Integer> nameTable = new HashMap<>();

    public void addNmae(String name) {
        int currentLocation = code.size();
        nameTable.put(name, currentLocation);
    }

    public void addCode(Instruction instruction) throws Exception {
        LOGGER.debug("add instruction" + instruction);
        byte[] bytes = instruction.toBytes();
        if (bytes != null) {
            for (byte b : bytes) {
                code.add(b);
            }
        }
    }

    public Byte get(int index) {
        if (index < code.size()) {
            return code.get(index);
        } else {
            return null;
        }
    }

    public int getLocationByName(String name) {
        return nameTable.getOrDefault(name, -1);
    }

    @Override
    public void resetAfterFirstParsingPass() {
        code.clear();
    }
}
