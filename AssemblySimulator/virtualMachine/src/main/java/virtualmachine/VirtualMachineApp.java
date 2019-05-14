package virtualmachine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"virtualmachine","config","tomasulo","render"})
public class VirtualMachineApp implements CommandLineRunner {
    private static Logger LOGGER = Logger.getLogger(VirtualMachineApp.class);

    private static VirtualMachine virtualMachine;

    @Autowired
    VirtualMachineApp(VirtualMachine virtualMachine) {
        VirtualMachineApp.virtualMachine = virtualMachine;
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtualMachineApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("init virtual machine ...");
        LOGGER.warn("For now, the virtual machine only support 32 bits registers.");
        if (args.length == 1) {
            String objFileName = args[0];
            virtualMachine.loadObjFile(objFileName);
            virtualMachine.run();
        } else {
            throw new Exception("must specify an obj file name");
        }

    }
}
