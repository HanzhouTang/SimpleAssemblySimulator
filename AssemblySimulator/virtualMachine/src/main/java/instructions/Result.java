package instructions;

/**
 * Every time, will create a new instruction from the old one.
 */
public class Result {
    private final Integer result;
    private final InstructionBase instructionBase;
    private final ResultState state;

    public enum ResultState {READ_COMPLETE, EXEC_COMPLETE, WRITE_COMPLETE, EXECUTING, READING, WRITING,COMPLETE}

    public Result(Integer o, InstructionBase instructionBase, ResultState s) {
        result = o;
        this.instructionBase = instructionBase;
        state = s;
    }

    public ResultState getState() {
        return state;
    }

    public InstructionBase getInstructionBase() {
        return instructionBase;
    }

    public Integer getResult() {
        return result;
    }
}
