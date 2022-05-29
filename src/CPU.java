import java.util.Random;

public class CPU implements Runnable{

    private final int RAND_MIN = 100;
    private final int RAND_MAX = 300;

    private final int id;

    private final CPUQueue queue;
    private CPUProcessScheduler cpuProcessScheduler;
    private final Random random = new Random();
    private final Monitor monitor;

    public CPU(int id, CPUQueue queue, CPUProcessScheduler cpuProcessScheduler, Monitor monitor) {
        this.id = id;
        this.queue = queue;
        this.cpuProcessScheduler = cpuProcessScheduler;
        this.monitor = monitor;
    }

    public CPU(int id, CPUQueue queue, Monitor monitor) {
        this.id = id;
        this.queue = queue;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        long processingTime;
        CPUProcess processToExecute = new CPUProcess(0, "0-0");
        boolean isNextProcFromTheQueue;
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            processingTime = RAND_MIN + random.nextInt(RAND_MAX);
            try {
                synchronized (monitor) {
                    if (getPendingProcess() != null) {
                        processToExecute = getPendingProcess();
                        cpuProcessScheduler.setPendingProcessForCPU(id, null);
                        cpuProcessScheduler.setCurrentProcessTypeForCPU(id, processToExecute.getType());
                        isNextProcFromTheQueue = false;
                    } else {
                        isNextProcFromTheQueue = true;
                    }
                }
                if (isNextProcFromTheQueue) {
                    processToExecute = queue.get();
                    if (processToExecute.getType() == 0) {
                        continue;
                    }
                }
                synchronized (monitor) {
                    cpuProcessScheduler.setCurrentProcessTypeForCPU(id, processToExecute.getType());
                }
                Thread.sleep(processingTime); // processing simulation
                System.out.println(String.format("Process with id %s was processed on CPU%s", processToExecute.getId(), id));
                synchronized (monitor) {
                    cpuProcessScheduler.setCurrentProcessTypeForCPU(id, 0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

    public CPUProcess getPendingProcess() {
        return cpuProcessScheduler.getPendingProcessForCPU(id);
    }

    public int getCPUId() {
        return id;
    }

    public void setCpuProcessScheduler(CPUProcessScheduler cpuProcessScheduler) {
        this.cpuProcessScheduler = cpuProcessScheduler;
    }

}
