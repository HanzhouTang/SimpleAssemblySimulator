package instructions;

/**
 * Every time, will create a new instruction from the old one.
 */
public class Result {
    private final Object result;
    private final InstructionBase instructionBase;
    private final ResultState state;

    enum ResultState {READ_COMPLETE, EXEC_COMPLETE, WRITE_COMPLETE}

    public Result(Object o, InstructionBase instructionBase, ResultState s) {
        result = o;
        this.instructionBase = instructionBase.copy();
        state = s;
    }

}
