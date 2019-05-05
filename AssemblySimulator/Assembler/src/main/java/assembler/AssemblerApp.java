package assembler;

import OutputFile.ObjFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry of the assembler.
 */

@SpringBootApplication
public class AssemblerApp implements CommandLineRunner {
    private static SimpleParser simpleParser;
    private static final Logger LOGGER = Logger.getLogger(AssemblerApp.class);

    @Autowired
    public void setSimpleParser(SimpleParser simpleParser) {
        LOGGER.debug("set simple parser");
        AssemblerApp.simpleParser = simpleParser;
    }

    public static void main(String[] args) {

        SpringApplication.run(AssemblerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("start assembler...");
        if (args.length == 1) {
            final String filename = args[0];
            //String ext = FilenameUtils.getExtension(filename);
            String baseName = FilenameUtils.getBaseName(filename);
            ObjFile obj = simpleParser.parse(filename);
            obj.dump(baseName + ".obj");
        } else if (args.length == 2) {
            final String filename = args[0];
            ObjFile obj = simpleParser.parse(filename);
            obj.dump(args[1]);
        } else {
            LOGGER.error("Must specify a input filename");
            System.exit(-1);
        }

    }
}
