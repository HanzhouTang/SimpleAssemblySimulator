package assembler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SimpleLexer implements Lexer {
    private static final Logger LOGGER = Logger.getLogger(SimpleLexer.class);
    private Map<Integer, String> lines = null;

    public Map<Integer, String> getLines() {
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
    }

    Status status = null;

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

    public void readFile(String filename) throws Exception {
        AtomicInteger counter = new AtomicInteger();
        lines = Files.lines(Paths.get(filename))
                .collect(Collectors.toMap(s -> counter.incrementAndGet(), s -> s))
                .entrySet().stream().map(this::stripComments).filter(x -> StringUtils.isNotEmpty(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (v1, v2) -> {
                            throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                        },
                        TreeMap::new));

        status = new Status(lines.entrySet().iterator());
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

    public Token getNextToken() throws Exception {
        if (!status.getIterator().hasNext()) {
            return Token.EndofContent;
        } else {
            if (status.getCharIndex() == status.getCurrentLine().length()) {
                Map.Entry<Integer, String> entry = status.getIterator().next();
                status.setCharIndex(0);
                status.setCurrentLine(entry.getValue());
                status.setLineIndex(entry.getKey());
                status.setCurrentToken(Token.Invalid);
                status.setCurrentLexeme("");
            }
            status.setCurrentToken(Token.Invalid);
            final String str = status.getCurrentLine();
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
                throw new Exception("the word (" + status.getCurrentLexeme() + ") at line " + status.getLineIndex() + " is invalid");
            }
            return status.getCurrentToken();
        }
    }

}
