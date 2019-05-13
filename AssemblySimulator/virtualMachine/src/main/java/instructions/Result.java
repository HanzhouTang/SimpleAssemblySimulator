package instructions;

/**
 * Every time, will create a new instruction from the old one.
 */
public class Result {
    private final Integer result;
    private final InstructionBase instructionBase;
    private final ResultState state;

    enum ResultState {READ_COMPLETE, EXEC_COMPLETE, WRITE_COMPLETE, EXECUTING, READING, WRITING}

    public Result(Integer o, InstructionBase instructionBase, ResultState s) {
        result = o;
        this.instructionBase = instructionBase;
        state = s;
    }

    public InstructionBase getInstructionBase() {
        return instructionBase;
    }

    public Integer getResult() {
        return result;
    }
}
