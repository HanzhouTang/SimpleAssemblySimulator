package virtualmachine;

import Instructions.Register;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Though the virtual machine will run different instructions in different thread,
 * there's no need to use ConcurrentHashMap or any other synchronized mechanism.
 * Because the virtual machine will strictly follow the Tomasulo’s algorithm which
 * means at each clock cycle, only one instruction can modify the value of registers, except pc register.
 *
 * @author Hanzhou Tang
 */
@Component
public class RegisterManager {
    private static Map<String, PhysicalRegister> registers = new HashMap<>();

    static {
        registers.put("pc", new PhysicalRegister("pc"));
        registers.put("eax", new PhysicalRegister("eax"));
        registers.put("ecx", new PhysicalRegister("ecx"));
        registers.put("ebx", new PhysicalRegister("ebx"));
        registers.put("edx", new PhysicalRegister("edx"));
        registers.put("esp", new PhysicalRegister("esp"));
        registers.put("ebp", new PhysicalRegister("ebp"));
        registers.put("esi", new PhysicalRegister("esi"));
        registers.put("edi", new PhysicalRegister("edi"));
    }

    public static PhysicalRegister getRegister_(Register r) {
        return registers.get(r.getRegisterName().toLowerCase());
    }

    public static PhysicalRegister getRegister_(String name) {
        return registers.get(name.toLowerCase());
    }

    public PhysicalRegister getRegister(String name) {
        return RegisterManager.getRegister_(name);
    }

    public PhysicalRegister getRegister(Register r) {
        return RegisterManager.getRegister_(r);
    }

    public void setRegisterValue(String name, int value) {
        getRegister(name).setContent(value);
    }

    public void setRegisterValue(Register r, int value) throws Exception {
        getRegister(r).setContent(value);
    }
}
