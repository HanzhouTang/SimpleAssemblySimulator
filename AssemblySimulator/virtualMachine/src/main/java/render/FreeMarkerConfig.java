package render;

import config.VirtualMachineProperties;
import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Paths;

@Component
public class FreeMarkerConfig {
    Configuration cfg = null;

    @Autowired
    VirtualMachineProperties virtualMachineProperties;

    FreeMarkerConfig() throws Exception {
        cfg = new Configuration();
        URI uri = getClass().getClassLoader().getResource("templates").toURI();
        cfg.setDirectoryForTemplateLoading(Paths.get(uri).toFile());
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    Writer getOutputWriter(String filename) throws Exception {
        String basePath = virtualMachineProperties.getOutputDir();
        String output = FilenameUtils.concat(basePath, filename);
        return new FileWriter(output);
    }

    Configuration getCfg() {
        return cfg;
    }
}
