package assembler;

import OutputFile.DataType;
import OutputFile.ObjFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Component
public class SimpleParser {
    @Autowired
    private Lexer lexer;
    private static final Logger LOGGER = Logger.getLogger(SimpleParser.class);

    public Lexer getLexer() {
        return lexer;
    }

    public ObjFile parse(String name) throws Exception {
        lexer.readFile(name);
        ObjFile objFile = new ObjFile();
        parse(objFile);
        return objFile;
    }

    protected void parse(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if (wrapper.getToken().equals(Token.DotString) && ".data".equals(wrapper.getLexeme())) {
            dataSegment(obj);
        }
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
        LOGGER.info("in data Define");
        dataName(obj);
        Token token = lexer.getNextToken();
        LOGGER.info("in data Define, token "+token);
        if (token.equals(Token.String)) {
            String lexem = lexer.getStatus().getCurrentLexeme();
            Optional<DataType> dataType = DataType.of(lexem);
            if (dataType.isPresent()) {
                dataList(obj, dataType.get());
            } else {
                throw new Exception("not find data type define in data segment at line " + lexer.getStatus().getLineIndex());
            }
        }
        LOGGER.info("exit data define");
    }

    protected void moreDataDefine(ObjFile obj) throws Exception {
        LexemeTokenWrapper wrapper = lexer.lookAheadK(1).get(0);
        if(wrapper.getToken().equals(Token.EndofContent)){
            return;
        }
        LOGGER.info("wrapper token "+wrapper.getToken()+" lexeme ("+wrapper.getLexeme()+")");
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
        } else if (wrapper.getToken().equals(Token.Number)) {
            match(Token.Number);
            BigInteger number = new BigInteger(lexer.getStatus().currentLexeme);
            obj.getDataSegment().addData(number, dataType);
        } else if (wrapper.getToken().equals(Token.String) && "dup".equals(wrapper.getLexeme())) {
            int currentLocation = obj.getDataSegment().getCurrentLocation();
            match(Token.String);
            match(Token.Number);
            int times = Integer.valueOf(lexer.getStatus().getCurrentLexeme());
            LOGGER.info("dup times "+times+" current location "+currentLocation);
            match(Token.LeftParent);
            dataList(obj, dataType);
            LOGGER.info("after parse data list location "+obj.getDataSegment().getCurrentLocation());
            List<Byte> tmpData = obj.getDataSegment().getPortionFrom(currentLocation);
            for (int i = 1; i < times; i++) {
                obj.getDataSegment().addData(tmpData);
            }
            match(Token.RightParent);
        }
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
