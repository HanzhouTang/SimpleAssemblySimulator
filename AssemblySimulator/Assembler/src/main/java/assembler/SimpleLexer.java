package assembler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The real implementation of Lexer interface.
 * The Simple will first strip all comments and then split content into different tokens by delimiters.
 * @author  Hanzhou Tang
 */
@Component
public class SimpleLexer implements Lexer {
    private static final Logger LOGGER = Logger.getLogger(SimpleLexer.class);
    //private Map<Integer, String> lines = null;
    private List<SourceCodeWrapper> lines = null;
    public List<SourceCodeWrapper> getLines() {
        return lines;
    }

    public static Set<Character> delimiters = new HashSet<>();
    public static Map<String, Token> char2Token = new HashMap<>();

    static {
        delimiters.add(' ');
        delimiters.add('\t');
        delimiters.add('\n');
        delimiters.add(',');
        delimiters.add(':');
        delimiters.add('+');
        delimiters.add('-');
        delimiters.add('*');
        delimiters.add('/');
        delimiters.add('[');
        delimiters.add(']');
        delimiters.add('(');
        delimiters.add(')');
        delimiters.add('$');
        delimiters.add('\'');
        char2Token.put(",", Token.Comma);
        char2Token.put(":", Token.Colon);
        char2Token.put("+", Token.Add);
        char2Token.put("-", Token.Sub);
        char2Token.put("*", Token.Mul);
        char2Token.put("/", Token.Div);
        char2Token.put("[", Token.LeftSquareBracket);
        char2Token.put("]", Token.RightSquareBracket);
        char2Token.put("\n", Token.NewLine);
        char2Token.put("(", Token.LeftParent);
        char2Token.put(")", Token.RightParent);
        char2Token.put("$", Token.DollarSign);
        char2Token.put("'",Token.Quote);
    }

    Status status = null;

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private Pair<Integer, String> stripComments(final Map.Entry<Integer, String> record) {
        String retStr = StringUtils.chomp(record.getValue());
        int index = retStr.indexOf(';');
        if (index != -1) {
            retStr = retStr.substring(0, index);
        }
        retStr = StringUtils.strip(retStr);
        if (StringUtils.isNotEmpty(retStr)) {
            retStr += "\n";
        }
        return Pair.of(record.getKey(), retStr);
    }

    @Override
    public void readFile(String filename) throws Exception {
        AtomicInteger counter = new AtomicInteger();
        lines = Files.lines(Paths.get(filename))
                .collect(Collectors.toMap(s -> counter.incrementAndGet(), s -> s))
                .entrySet().stream().map(this::stripComments).filter(x -> StringUtils.isNotEmpty(x.getValue()))
                .map(e -> new SourceCodeWrapper(e.getKey(),e.getValue())).collect(Collectors.toList());
        if(lines.size()>0){
            status = new Status();
            status.setCodeOfCurrentLine(lines.get(0).getCode());
        }
        else{
            throw  new Exception("file is empty");
        }
    }


    private LexemeTokenWrapper getNextLexemeAndToken(){
        Token token = getNextToken();
        if(token.equals(Token.EndofContent)){
            return new LexemeTokenWrapper(Token.EndofContent,"");
        }
        else{
            return new LexemeTokenWrapper(getStatus().getCurrentToken(),getStatus().getCurrentLexeme());
        }

    }

    @Override
    public List<LexemeTokenWrapper> lookAheadK(int k){
        Status oldStatus = new Status(getStatus());
        List<LexemeTokenWrapper> ret = Stream.generate(this::getNextLexemeAndToken).limit(k).collect(Collectors.toList());
        setStatus(oldStatus);
        return ret;
    }

    public static int findStr_if(final String str, final int begin, Predicate<Character> predicate) {
        int i = begin;
        for (; i < str.length(); i++) {
            if (predicate.test(str.charAt(i))) {
                return i;
            }
        }
        return i;
    }

    @Override
    public Token getNextToken() {
        if (status.getIterator()>=lines.size()) {
            return Token.EndofContent;
        } else {
            if (status.getCharIndex() == status.getCodeOfCurrentLine().length()) {
                status.setIterator(status.getIterator()+1);
                if(status.getIterator()==lines.size()){
                    return Token.EndofContent;
                }
                SourceCodeWrapper wrapper = lines.get(status.getIterator());
                status.setCharIndex(0);
                status.setCodeOfCurrentLine(wrapper.getCode());
                status.setLineIndex(wrapper.getLineNumber());
                status.setCurrentToken(Token.Invalid);
                status.setCurrentLexeme("");
            }
            status.setCurrentToken(Token.Invalid);
            final String str = status.getCodeOfCurrentLine();
            int i = findStr_if(str, status.getCharIndex(), ch -> ch == '\n' || !Character.isWhitespace(ch));
            int j = findStr_if(str, i, delimiters::contains);
            if (i == j) {
                status.setCurrentLexeme("" + str.charAt(i));
                status.setCharIndex(i + 1);
                if (char2Token.containsKey(status.getCurrentLexeme())) {
                    Token token = char2Token.get(status.getCurrentLexeme());
                    status.setCurrentToken(token);
                } else {
                    LOGGER.error("the Character (" + status.getCurrentLexeme() + ") " + "at line " + status.getLineIndex() + " is invalid");
                }
            } else {
                String lexeme = str.substring(i, j);
                status.setCurrentLexeme(lexeme);
                status.setCharIndex(j);
                if (StringUtils.isNumeric(lexeme)) {
                    status.setCurrentToken(Token.Number);
                } else if (lexeme.startsWith(".")) {
                    status.setCurrentToken(Token.DotString);
                } else {
                    status.setCurrentToken(Token.String);
                }
            }
            if (status.getCurrentToken() == Token.Invalid) {
                LOGGER.warn("the word (" + status.getCurrentLexeme() + ") at line " + status.getLineIndex() + " is invalid");
                //throw new Exception("the word (" + status.getCurrentLexeme() + ") at line " + status.getLineIndex() + " is invalid");
            }
            return status.getCurrentToken();
        }
    }

}
