import java.util.Random;

public abstract class CPUProcessGenerator implements Runnable {

    protected int cpuProcessType;
    protected int generateNumber;
    protected CPUProcessScheduler cpuProcessScheduler;

    protected final Random random = new Random();
    protected int RAND_MAX = 10;
    protected int RAND_MIN = 40; // rand = [10,50]

    public CPUProcessGenerator(int cpuProcessType, int generateNumber, CPUProcessScheduler cpuProcessScheduler) {
        this.cpuProcessType = cpuProcessType;
        this.generateNumber = generateNumber;
        this.cpuProcessScheduler = cpuProcessScheduler;
    }

}
