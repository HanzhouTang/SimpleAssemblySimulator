package assembler;

import Instructions.*;
import OutputFile.CodeSegment;
import OutputFile.DataType;
import OutputFile.ObjFile;
import OutputFile.Procedure;
import org.apache.log4j.Logger;
import org.omg.CORBA.TRANSACTION_MODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * A parser to convert a assembly file into binary file.
 * For now, the parser will pass code segment twice.
 * It will collect all label information in the first time.
 * In the second time, the real parsing is done.
 * <p>
 * For convenient, the compiler require data segment always come before code segment.
 * Cannot put directive and instruction in the same line
 * TODO: support code segment and data segment in any order.
 *
 * @author Hanzhou Tang
 */

@Component
public class SimpleParser {
    @Autowired
    private Lexer lexer;
    private static final Logger LOGGER = Logger.getLogger(SimpleParser.class);
    private boolean firstPass = true;
    private boolean isEnd = false;
    private Stack<Procedure.PartialProcedure> procedureStack = new Stack<>();

    protected void setFirstPass(boolean b) {
        firstPass = b;
    }

    public boolean getFirstPass() {
        return firstPass;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public ObjFile parse(String name) throws Exception {
        setFirstPass(true);
        lexer.readFile(name);
        ObjFile objFile = new ObjFile();
        parse(objFile);
        if (!isEnd) {
            LOGGER.warn("warning: didn't find end directive. Cannot set entry point");
        }
        if(procedureStack.size()>0){
            Procedure.PartialProcedure p = procedureStack.peek();
            throw new Exception("the procedure <"+p.getName()+"> not end");
        }
        lexer.readFile(name); // reset the lexer.
        objFile.resetAfterFirstParsingPass();
        setFirstPass(false); // not the first pass
        isEnd = false;
        parse(objFile);
        return objFile;
    }


    protected void parse(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.EndofContent)) {
            return;
        } else if (wrapper.getToken().equals(Token.NewLine)) {
            match(Token.NewLine);
        } else if (wrapper.getToken().equals(Token.DotString)) {
            if (".data".equalsIgnoreCase(wrapper.getLexeme())) {
                dataSegment(obj);
            } else if (".code".equalsIgnoreCase(wrapper.getLexeme())) {
                codeSegment(obj);
            } else {
                throw new UnsupportedOperationException("the " + wrapper.getLexeme() + " segment at line "
                        + wrapper.getLineIndex() + " is not supported for now.");
            }
        } else {
            throw new Exception("expected segment define at line " + wrapper.getLineIndex() + ". However, got " + wrapper.getLexeme());
        }
        parse(obj);
    }

    protected void dataName(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.String)) {
            Optional<DataType> dataType = DataType.of(wrapper.lexeme);
            if (!dataType.isPresent()) {
                obj.getDataSegment().addNmae(wrapper.getLexeme());
                lexer.getNextToken();
                dataName(obj);
            }
        }
    }

    protected void dataDefine(ObjFile obj) throws Exception {
        LOGGER.debug("in data Define");
        dataName(obj);
        Token token = lexer.getNextToken();
        LOGGER.debug("in data Define, token " + token);
        if (token.equals(Token.String)) {
            String lexem = lexer.getStatus().getCurrentLexeme();
            Optional<DataType> dataType = DataType.of(lexem);
            if (dataType.isPresent()) {
                dataList(obj, dataType.get());
            } else {
                throw new Exception("not find data type define in data segment at line " + lexer.getStatus().getLineIndex());
            }
        }
        LOGGER.debug("exit data define");
    }

    protected void moreDataDefine(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.EndofContent)) {
            return;
        }
        LOGGER.debug("wrapper token " + wrapper.getToken() + " lexeme (" + wrapper.getLexeme() + ")");
        if (wrapper.getToken().equals(Token.NewLine)) {
            match(Token.NewLine);
        }
        wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken() != Token.DotString) {
            dataDefine(obj);
            moreDataDefine(obj);
        }
    }

    protected void dataList(ObjFile obj, DataType dataType) throws Exception {
        data(obj, dataType);
        moreData(obj, dataType);

    }

    protected void match(String str) throws Exception {
        lexer.getNextToken();
        if (!str.equals(lexer.getStatus().getCurrentLexeme())) {
            throw new Exception("Expected " + str + " at line " + lexer.getStatus().getLineIndex() +
                    ". However, got " + lexer.getStatus().getCurrentLexeme());
        }
    }

    protected void match(Token t) throws Exception {
        Token token = lexer.getNextToken();
        if (!t.equals(token)) {
            throw new Exception("Expected token <" + t + "> at line " + lexer.getStatus().getLineIndex() +
                    ". However, got <" + token+">");
        }
    }

    /**
     * For now, only supports 3 kinds of data.
     * They are string, like '1dfdssasad'.
     * Number, like 123.
     * And dup, like dup 10 (123).
     * We should support $ and basic arithmetic operations here later.
     *
     * @param obj      the return object file
     * @param dataType see Enum dataType
     * @throws Exception exception
     */
    protected void data(ObjFile obj, DataType dataType) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.Quote)) {
            match(Token.Quote);
            match(Token.String);
            obj.getDataSegment().addData(lexer.getStatus().currentLexeme, dataType);
            match(Token.Quote);
        } else if (wrapper.getToken().equals(Token.String) && "dup".equals(wrapper.getLexeme())) {
            int currentLocation = obj.getDataSegment().getCurrentLocation();
            match(Token.String);
            match(Token.Number);
            int times = Integer.valueOf(lexer.getStatus().getCurrentLexeme());
            LOGGER.debug("dup times " + times + " current location " + currentLocation);
            match(Token.LeftParent);
            dataList(obj, dataType);
            LOGGER.debug("after parse data list location " + obj.getDataSegment().getCurrentLocation());
            List<Byte> tmpData = obj.getDataSegment().getPortionFrom(currentLocation);
            for (int i = 1; i < times; i++) {
                obj.getDataSegment().addData(tmpData);
            }
            match(Token.RightParent);
        } else {
            BigInteger number = expr(obj);
            obj.getDataSegment().addData(number, dataType);
        }
    }


    protected BigInteger expr(ObjFile obj) throws Exception {
        BigInteger term = term(obj);
        return moreTerm(obj, term);
    }

    protected BigInteger term(ObjFile obj) throws Exception {
        BigInteger factor = factor(obj);
        return moreFactor(obj, factor);
    }

    //negative is not very good here
    protected BigInteger moreTerm(ObjFile obj, BigInteger left) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.Add)) {
            match(Token.Add);
            BigInteger right = term(obj);
            return moreFactor(obj, left.add(right));
        } else if (wrapper.getToken().equals(Token.Sub)) {
            match(Token.Sub);
            BigInteger right = term(obj);
            return moreFactor(obj, left.subtract(right));
        }
        return left;
    }


    protected BigInteger factor(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        BigInteger number = null;
        if (wrapper.getToken().equals(Token.Sub)) {
            match(Token.Sub);
            return factor(obj).negate();
        } else if (wrapper.getToken().equals(Token.LeftParent)) {
            match(Token.LeftParent);
            number = expr(obj);
            match(Token.RightParent);
            return number;
        }
        wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.Number)) {
            match(Token.Number);
            number = new BigInteger(lexer.getStatus().currentLexeme);
        } else if (wrapper.getToken().equals(Token.String)) {
            match(Token.String);
            final String str = lexer.getStatus().getCurrentLexeme();
            if ("sizeof".equals(str)) {
                match(Token.String);
                Optional<DataType> type = DataType.of(lexer.getStatus().getCurrentLexeme());
                if (!type.isPresent()) {
                    throw new Exception("not find data type define after sizeof operator at line " + wrapper.getLineIndex());
                } else {
                    number = BigInteger.valueOf(type.get().getSize());
                }
            } else {
                if (firstPass) {
                    number = BigInteger.valueOf(0);
                } else {
                    int location = obj.getDataSegment().getLocationByName(str);
                    if (location == -1) {
                        throw new Exception("not find label <" + str + "> at line " + wrapper.getLineIndex());
                    } else {
                        number = BigInteger.valueOf(location);
                    }
                }
            }
        } else if (wrapper.getToken().equals(Token.DollarSign)) {
            match(Token.DollarSign);
            int location = obj.getDataSegment().getCurrentLocation();
            number = BigInteger.valueOf(location);
        }
        if (number == null) {
            throw new Exception("Number is not defined at line " + wrapper.getLineIndex());
        }
        return number;
    }


    protected BigInteger moreFactor(ObjFile obj, BigInteger left) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.Mul)) {
            match(Token.Mul);
            BigInteger right = factor(obj);
            return moreFactor(obj, left.multiply(right));
        } else if (wrapper.getToken().equals(Token.Div)) {
            match(Token.Div);
            BigInteger right = factor(obj);
            return moreFactor(obj, left.divide(right));
        }
        return left;
    }

    public void moreData(ObjFile obj, DataType dataType) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.Comma)) {
            match(Token.Comma);
            data(obj, dataType);
            moreData(obj, dataType);
        }
    }


    protected void dataSegment(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.token.equals(Token.DotString) && ".data".equals(wrapper.getLexeme())) {
            lexer.getNextToken();
        }
        moreDataDefine(obj);
    }

    protected void codeSegment(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.token.equals(Token.DotString) && ".code".equals(wrapper.getLexeme())) {
            lexer.getNextToken();
        }
        moreCodeExpress(obj);
    }

    protected void codeExpress(ObjFile obj) throws Exception {
        if (!isEnd && !directive(obj)) {
            label(obj);
            instruction(obj);
        }
    }

    protected void moreCodeExpress(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (isEnd || wrapper.getToken().equals(Token.EndofContent)) {
            return;
        }
        LOGGER.debug("wrapper token " + wrapper.getToken() + " lexeme (" + wrapper.getLexeme() + ")");
        if (wrapper.getToken().equals(Token.NewLine)) {
            match(Token.NewLine);
        }
        wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken() != Token.DotString) {
            codeExpress(obj);
            LOGGER.debug("parse next instruction");
            moreCodeExpress(obj);
        }
    }

    protected void label(ObjFile obj) throws Exception {
        List<LexemeTokenWrapper> wrappers = lexer.lookAheadK(2);
        if (Token.String.equals(wrappers.get(0).getToken()) && Token.Colon.equals(wrappers.get(1).getToken())) {
            String label = wrappers.get(0).getLexeme();
            match(Token.String);
            match(Token.Colon);
            obj.getCodeSegment().addLabel(label);
        }
    }

    protected void instructionDisplacementChecker(Operand.Builder builder) throws Exception {
        Mode mode = builder.getMode();
        if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) ||
                Mode.INDIRECT_DISPLACEMENT_FOLLOWED.equals(mode) ||
                Mode.DISPLACEMENT_ONLY.equals(mode)) {
            throw new Exception("one operand can only have one displacement");
        }
    }

    protected void indirectInstructionNumberTerm(Operand.Builder builder, ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        boolean isReversed = false;
        if (Token.Sub.equals(wrapper.getToken())) {
            match(Token.Sub);
            isReversed = true;
        }
        match(Token.Number);
        int number = Integer.valueOf(lexer.getStatus().getCurrentLexeme());
        if (isReversed) {
            number = -number;
        }
        LOGGER.debug("number " + number);
        instructionDisplacementChecker(builder);
        builder.displacement(number);
        moreInstructionTerm(builder, obj);
    }


    protected void moreInstructionTerm(Operand.Builder builder, ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (Token.Sub.equals(wrapper.getToken())) {
            indirectInstructionNumberTerm(builder, obj);
        } else if (Token.Add.equals(wrapper.getToken())) {
            match(Token.Add);
            instructionTerm(builder, obj);
        }
    }


    protected void instructionTerm(Operand.Builder builder, ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (Token.Sub.equals(wrapper.getToken()) || Token.Number.equals(wrapper.getToken())) {
            indirectInstructionNumberTerm(builder, obj);
        } else if (Token.String.equals(wrapper.getToken())) {
            String str = wrapper.getLexeme();
            match(Token.String);
            Optional<Register> register = Register.fromName(str);
            if (register.isPresent()) {
                wrapper = lexer.lookAheadK(1).get(0);
                if (Token.Mul.equals(wrapper.getToken())) {
                    match(Token.Mul);
                    match(Token.Number);
                    int scale = Integer.valueOf(lexer.getStatus().getCurrentLexeme());
                    Mode mode = builder.getMode();
                    if (Mode.SIB_DISPLACEMENT_FOLLOWED.equals(mode) || Mode.SIB.equals(mode)) {
                        throw new Exception("can only have one index and one scale in one operand");
                    }
                    builder.index(register.get()).scale(scale);
                } else {
                    builder.base(register.get());
                }
            } else {
                // displacement
                // Here is a problem. if the displacement larger than 127. It should be a 4 byte displacement instead of 0.
                int displacement = obj.getDataSegment().getLocationByName(str);
                instructionDisplacementChecker(builder);
                if (displacement == -1) {
                    throw new Exception("invalid displacement label <" + str + "> at line " + wrapper.getLineIndex());
                }
                builder.displacement(displacement);

            }
            moreInstructionTerm(builder, obj);
        }

    }

    protected Operand indirect(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        Operand.Builder builder = new Operand.Builder();
        instructionTerm(builder, obj);
        return builder.build();
    }

    private int getLabelOrProcedureLocation(ObjFile obj, String str) {
        CodeSegment segment = obj.getCodeSegment();
        int location = segment.getLocationByLabel(str);
        if (location != -1) {
            return location;
        }
        location = segment.getProcedureEntryPoint(str);
        if (location != -1) {
            return location;
        }
        return -1;
    }

    protected Operand operand(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (Token.LeftSquareBracket.equals(wrapper.getToken())) {
            match(Token.LeftSquareBracket);
            Operand ret = indirect(obj);
            match(Token.RightSquareBracket);
            return ret;
        } else if (Token.String.equals(wrapper.getToken())) {
            LOGGER.debug("register mode");
            match(Token.String);
            String str = wrapper.getLexeme();
            Optional<Register> register = Register.fromName(str);
            if (register.isPresent()) {
                return new Operand.Builder().register(register.get()).build();
            } else {
                if (firstPass) {
                    // need check if one byte immediate and 4 byte immediate are different.
                    return new Operand.Builder().immediate(0).build();
                } else {
                    int location = getLabelOrProcedureLocation(obj, str);
                    if (location != -1) {
                        return new Operand.Builder().immediate(location).build();
                    }
                    location = obj.getDataSegment().getLocationByName(str);
                    if(location!=-1){
                        return new Operand.Builder().immediate(location).build();
                    }
                    throw new Exception("the label <" + str + "> at line " + wrapper.getLineIndex() + " is not existed");
                }
            }
        } else if (Token.Number.equals(wrapper.getToken()) || Token.Sub.equals(wrapper.getToken())) {
            boolean isReversed = false;
            if (Token.Sub.equals(wrapper.getToken())) {
                match(Token.Sub);
                isReversed = true;
            }
            match(Token.Number);
            int number = Integer.valueOf(lexer.getStatus().getCurrentLexeme());
            if (isReversed) {
                number = -number;
            }
            return new Operand.Builder().immediate(number).build();

        }
        else if(Token.NewLine.equals(wrapper.getToken())){
            return null;
            // for ret instruction who doesn't have any operands.
        }
        throw new Exception("invalid token <" + wrapper.getToken() + "> for operand " + " at line " + wrapper.getLineIndex());
    }

    protected void instruction(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (Token.String.equals(wrapper.getToken())) {
            String op = wrapper.getLexeme();
            Optional<Op> opcode = OpCode.fromMem(op);
            if (!opcode.isPresent()) {
                throw new Exception("unsupported instruction <" + wrapper.getLexeme() + "> at line " + wrapper.getLineIndex());
            }
            LOGGER.debug("opcode " + opcode.get());
            match(Token.String);
            Operand dest = operand(obj);
            LOGGER.debug("operand dest " + dest);
            wrapper = lexer.lookAheadK(1).get(0);
            //LOGGER.debug("next token "+wrapper.getToken());
            if (Token.Comma.equals(wrapper.getToken())) {
                match(Token.Comma);
                Operand source = operand(obj);
                LOGGER.debug("operand source " + source);
                Instruction instruction = new Instruction(opcode.get(), dest, source);
                LOGGER.debug("instruction {" + instruction + " }");
                obj.getCodeSegment().addInstruction(instruction);
            } else {
                // for unary instruction
                Instruction instruction = new Instruction(opcode.get(), null, dest);
                LOGGER.debug("instruction {" + instruction + " }");
                obj.getCodeSegment().addInstruction(instruction);
            }
            match(Token.NewLine);

        }
        // need a little time to parse operand.
        // I guess it may take me 2 hour
    }

    /**
     * directive support.
     * For now, only supports proc endp and end.
     * For now, The assembler supports define a procedure inside another procedure
     * f(){
     * g(){
     * <p>
     * }
     * }
     * This is legal
     * // end must followed by entry point
     * directive must ended by newline '\n'
     */
    protected boolean directive(ObjFile objFile) throws Exception {
        List<LexemeTokenWrapper> wrappers = lexer.lookAheadK(2);
        if (Token.String.equals(wrappers.get(0).getToken()) && Token.String.equals(wrappers.get(1).getToken())) {
            if ("proc".equalsIgnoreCase(wrappers.get(1).getLexeme())) {
                String procName = wrappers.get(0).getLexeme();
                int location = objFile.getCodeSegment().getCurrentLocation();
                LOGGER.debug("for procedure <" + procName + "> start location " + location);
                Procedure.PartialProcedure proc = new Procedure.PartialProcedure(procName, location);
                procedureStack.push(proc);
                match(Token.String);
                match(Token.String);
                match(Token.NewLine);
                return true;
            } else if ("endp".equalsIgnoreCase(wrappers.get(1).getLexeme())) {
                String procName = wrappers.get(0).getLexeme();
                if (procedureStack.empty()) {
                    throw new Exception("end procedure <" + procName + "> before declare it, at line " + wrappers.get(0).getLineIndex());
                }
                if (!procedureStack.peek().getName().equalsIgnoreCase(procName)) {
                    throw new Exception("end procedure <" + procName + "> before declare it, at line " + wrappers.get(0).getLineIndex());
                }
                Procedure.PartialProcedure partialProcedure = procedureStack.pop();
                int location = objFile.getCodeSegment().getCurrentLocation();
                LOGGER.debug("for procedure " + procName + " end location " + location);
                Procedure p = partialProcedure.build(location);
                if (firstPass) {
                    objFile.getCodeSegment().addProcedure(p);
                }
                match(Token.String);
                match(Token.String);
                match(Token.NewLine);
                return true;
            }
        }
        if ("end".equalsIgnoreCase(wrappers.get(0).getLexeme())) {
            LOGGER.debug("end ");
            if (Token.String.equals(wrappers.get(1).getToken())) {
                String entryPoint = wrappers.get(1).getLexeme();
                if (!firstPass) {
                    objFile.getCodeSegment().setEntryPoint(entryPoint);
                }
            } else {
                throw new Exception("must define entry point of code segment after end directive");
            }
            isEnd = true;
            match(Token.String);
            match(Token.String);
            match(Token.NewLine);
            return true;
        }
        return false;
    }

}
