package tomasulo;

import Instructions.Register;

public class AddressEntry {
    enum Type {MEMORY, REGISTER, STACK}

    final Register register;
    final Integer memoryAddress;
    final Type type;

    public Type getType() {
        return type;
    }

    public AddressEntry(Register register) {
        this.register = register;
        this.memoryAddress = null;
        type = Type.REGISTER;
    }

    public AddressEntry() {
        type = Type.STACK;
        register = null;
        memoryAddress = null;
    }

    public AddressEntry(Integer i) {
        this.memoryAddress = i;
        this.register = null;
        type = Type.MEMORY;
    }

    public Integer getMemoryAddress() {
        return memoryAddress;
    }

    public Register getRegister() {
        return register;
    }

    @Override
    public String toString() {
        if (Type.REGISTER.equals(type)) {
            return register.toString();
        }
        return memoryAddress.toString();
    }
}
