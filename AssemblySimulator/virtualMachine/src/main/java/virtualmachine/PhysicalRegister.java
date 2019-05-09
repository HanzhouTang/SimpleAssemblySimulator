package virtualmachine;

import Instructions.Register;

public class PhysicalRegister {
    private final String registerName;
    private volatile int content;

    public void setContent(int c) {
        content = c;
    }

    public int getContent() {
        return content;
    }

    public PhysicalRegister(Register r) {
        registerName = r.getRegisterName();
    }

    public PhysicalRegister(String name) {
        registerName = name;
    }

    public String getRegisterName() {
        return registerName;
    }


}
