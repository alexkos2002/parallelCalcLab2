
public class CPU2ProcessGenerator extends CPUProcessGenerator{

    private Monitor CPU2Monitor;
    private CPUQueue queue;

    public CPU2ProcessGenerator(int cpuProcessType, int generateNumber, CPUProcessScheduler cpuProcessScheduler,
                                Monitor CPU2Monitor, CPUQueue queue) {
        super(cpuProcessType, generateNumber, cpuProcessScheduler);
        this.CPU2Monitor = CPU2Monitor;
        this.queue = queue;
    }

    @Override
    public void run() {
        int generateDelay;
        String cpuProcessId;
        int CPU2Id = CPU2Monitor.getId();
        boolean shouldGenProcBePutToQueue;
        for (int i = 0; i < generateNumber; i++) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            generateDelay = RAND_MIN + random.nextInt(RAND_MAX);
            cpuProcessId = String.format("%s-%s", cpuProcessType, i);
            try {
                Thread.sleep(generateDelay); // simulating process generating
                System.out.println(String.format(Constants.PROCESS_GENERATED_MESSAGE, cpuProcessId, generateDelay));
                CPUProcess genProcess = new CPUProcess(cpuProcessType, cpuProcessId);

                synchronized (CPU2Monitor) {
                    if (cpuProcessScheduler.getPendingProcessForCPU(CPU2Id) == null) {
                        cpuProcessScheduler.setPendingProcessForCPU(CPU2Id, genProcess);
                        shouldGenProcBePutToQueue = false;
                        System.out.println(String.format(Constants.PROCESS_BECOME_PENDING_MESSAGE,
                                cpuProcessId, CPU2Id));
                    } else {
                        shouldGenProcBePutToQueue = true;
                        System.out.println(String.format(Constants.PROCESS_ADDED_TO_QUEUE_MESSAGE, cpuProcessId));
                    }
                }

                if (shouldGenProcBePutToQueue) {
                    queue.put(genProcess);
                } else {
                    queue.notifyAllGetPerformers();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        System.out.println(String.format("No more processes of type %s.", cpuProcessType));
    }

}