package config;

import Instructions.Instruction;
import Instructions.Op;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@Component
@Configuration
@PropertySource(value = "classpath:setting.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "machine")
public class VirtualMachineProperties {
    private Map<String, Integer> executionTime = new HashMap<>();
    private Integer loadMemoryNeededCycle;
    private Integer saveMemoryNeededCycle;
    private Integer reorderBufferSize;
    private Integer reservationStationSize;
    private String outputDir;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String dir) {
        outputDir = dir;
    }

    public Integer getReservationStationSize() {
        return reservationStationSize;
    }

    public void setReservationStationSize(Integer reservationStationSize) {
        this.reservationStationSize = reservationStationSize;
    }

    public Integer getReorderBufferSize() {
        return reorderBufferSize;
    }

    public Integer getSaveMemoryNeededCycle() {
        return saveMemoryNeededCycle;
    }

    public void setSaveMemoryNeededCycle(Integer saveMemoryNeededCycle) {
        this.saveMemoryNeededCycle = saveMemoryNeededCycle;
    }

    public void setReorderBufferSize(Integer reorderBufferSize) {
        this.reorderBufferSize = reorderBufferSize;
    }

    public Integer getExecutionTime(Op opcode) {
        return executionTime.get(opcode.getMemonic().toLowerCase());
    }

    public void setExecutionTime(Map<String, Integer> executionTime) {
        this.executionTime = executionTime;
    }

    public void setLoadMemoryNeededCycle(Integer i) {
        loadMemoryNeededCycle = i;
    }

    public Integer getLoadMemoryNeededCycle() {
        return loadMemoryNeededCycle;
    }
}
