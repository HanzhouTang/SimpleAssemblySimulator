package assembler;

import OutputFile.DataType;
import OutputFile.ObjFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * A parser to convert a assembly file into binary file.
 * For now, the parser will pass code segment twice.
 * It will collect all label information in the first time.
 * In the second time, the real parsing is done.
 *
 * @author Hanzhou Tang
 */

@Component
public class SimpleParser {
    @Autowired
    private Lexer lexer;
    private static final Logger LOGGER = Logger.getLogger(SimpleParser.class);
    private boolean firstPass = true;

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
        lexer.readFile(name); // reset the lexer.
        objFile.resetAfterFirstParsingPass();
        setFirstPass(false); // not the first pass
        parse(objFile);
        return objFile;
    }



    protected void parse(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.EndofContent)){
            return;
        }
        else if(wrapper.getToken().equals(Token.NewLine)){
            match(Token.NewLine);
        }
        else if(wrapper.getToken().equals(Token.DotString)){
            if(".data".equalsIgnoreCase(wrapper.getLexeme())){
                dataSegment(obj);
            }
            else{
                throw  new UnsupportedOperationException("the "+wrapper.getLexeme()+" segment at line "
                        +wrapper.getLineIndex()+" is not supported for now.");
            }
        }
        else{
            throw new Exception("expected segment define at line "+wrapper.getLineIndex()+". However, we got "+wrapper.getLexeme());
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
                    ". However, we got " + lexer.getStatus().getCurrentLexeme());
        }
    }

    protected void match(Token t) throws Exception {
        Token token = lexer.getNextToken();
        if (!t.equals(token)) {
            throw new Exception("Expected token " + t + " at line " + lexer.getStatus().getLineIndex() +
                    ". However, we got " + token);
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
                        throw new Exception("not find label " + str + " at line " + wrapper.getLineIndex());
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
            throw new Exception(" number is not defined at line " + wrapper.getLineIndex());
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
}
