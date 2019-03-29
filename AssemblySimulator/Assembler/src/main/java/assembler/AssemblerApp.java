package assembler;

import org.apache.commons.io.FilenameUtils;

public class AssemblerApp {
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            final String filename = args[0];
            String ext = FilenameUtils.getExtension(filename);
            if (!ext.equals("asm")) {
                System.out.println("Must be an assembly file");
                System.exit(-1);
            } else {
                SimpleLexer lexer = new SimpleLexer();
                lexer.readFile(filename);
                Token token = Token.Invalid;
                while (token != Token.EndofContent) {
                    token = lexer.getNextToken();
                    if (token == Token.NewLine) {
                        System.out.println(" Line: " + lexer.getStatus().getLineIndex() + " Token: " + token.name());
                    } else {
                        System.out.println(" Line: " + lexer.getStatus().getLineIndex() + " Lexeme: "
                                + lexer.getStatus().getCurrentLexeme() + " Token: " + token.name());
                    }
                }
            }
        } else {
            System.out.println("Must specify a input filename");
            System.exit(-1);
        }
    }
}
