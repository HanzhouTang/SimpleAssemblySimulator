package virtualmachine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class VirtualMachine {
    @Autowired
    RegisterManager registerManager;

    @Autowired
    Memory memory;

    private static Logger LOGGER = Logger.getLogger(VirtualMachine.class);

    public void loadObjFile(String name) throws Exception {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(name))) {
            int checker = dataInputStream.readInt();
            if (checker != 0x7f) {
                throw new Exception("not a valid obj file");
            }
            int baseAddress = dataInputStream.readInt();
            int entryPoint = dataInputStream.readInt();
            List<Byte> data = new ArrayList<>();
            byte[] bytes = new byte[4096];
            int len;
            while ((len = dataInputStream.read(bytes)) > 0) {
                Byte[] tmp = new Byte[len];
                for (int i = 0; i < len; i++) {
                    tmp[i] = bytes[i];
                }
                data.addAll(Arrays.asList(tmp));
            }
            memory.setData(data);
            PhysicalRegister pc = registerManager.getRegister("pc");
            pc.setContent(entryPoint);
            LOGGER.info("total read size:     \t" + memory.size());
            LOGGER.info("entry point address: \t" + pc.getContent());
        } catch (Exception e) {
            throw e;
        }
    }
}
