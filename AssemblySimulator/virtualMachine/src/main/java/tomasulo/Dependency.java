package tomasulo;

import virtualmachine.RegisterManager;

public abstract class Dependency {
    public abstract Integer getNeededReorderBufferNumber(ReservedTable table, RegisterManager registerManager);
    public abstract Integer getAddress();
}
