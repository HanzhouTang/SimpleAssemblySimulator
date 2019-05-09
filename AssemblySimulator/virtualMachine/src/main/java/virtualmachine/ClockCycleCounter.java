package virtualmachine;

import org.springframework.stereotype.Component;

/**
 * The clock cycle counter will record clock cycle count in the VM.
 * Because all instruction thread will hold a reference to clock cycle counter.
 * It has be volatile or synchronized. To reduce cost, I choose volatile keyword.
 * Be caution, only VM can call nextClockClcye method.
 * @author Hanzhou Tang
 */
@Component
public class ClockCycleCounter {
    volatile int counter = 0;

    public int getCurrentClockCycle() {
        return counter;
    }

    void toNextClockCycle() {
        counter++;
    }
}
