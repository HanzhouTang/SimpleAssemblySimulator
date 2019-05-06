package virtualmachine;

import Instructions.Register;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegisterManager {
    private static Map<String, PhysicalRegister> registers = new HashMap<>();

    static {
        registers.put("pc", new PhysicalRegister("pc"));
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
}
