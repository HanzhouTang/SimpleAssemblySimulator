package tomasulo;

import virtualmachine.RegisterManager;

public abstract class Dependency {
    public abstract Integer getNeededReorderBufferNumber(ReversedTable table, RegisterManager registerManager);
    public abstract Integer getAddress();
}
