public class CPU1ProcessGenerator extends CPUProcessGenerator{

    private Monitor CPU1Monitor;
    private Monitor CPU2Monitor;
    private CPUQueue queue;

    public CPU1ProcessGenerator(int cpuProcessType, int generateNumber, CPUProcessScheduler cpuProcessScheduler,
                                Monitor CPU1Monitor, Monitor CPU2Monitor, CPUQueue queue) {
        super(cpuProcessType, generateNumber, cpuProcessScheduler);
        this.CPU1Monitor = CPU1Monitor;
        this.CPU2Monitor = CPU2Monitor;
        this.queue = queue;
    }

    @Override
    public void run() {
        int generateDelay;
        String cpuProcessId;
        int CPU1Id = CPU1Monitor.getId();
        int CPU2Id = CPU2Monitor.getId();
        boolean shouldQueueGetPerformersBeNotified;
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

                synchronized (CPU1Monitor) {
                    synchronized (CPU2Monitor) {
                        if (cpuProcessScheduler.getPendingProcessForCPU(CPU1Id) == null) {
                            cpuProcessScheduler.setPendingProcessForCPU(CPU1Id, genProcess);
                            shouldQueueGetPerformersBeNotified = true;
                            System.out.println(String.format(Constants.PROCESS_BECOME_PENDING_MESSAGE,
                                    cpuProcessId, CPU1Id));
                        } else if (cpuProcessScheduler.getPendingProcessForCPU(CPU1Id) != null &&
                                cpuProcessScheduler.getPendingProcessForCPU(CPU1Id).getType() == 1 &&
                                cpuProcessScheduler.getPendingProcessForCPU(CPU2Id) == null) {
                            cpuProcessScheduler.setPendingProcessForCPU(CPU2Id, genProcess);
                            shouldQueueGetPerformersBeNotified = true;
                            System.out.println(String.format(Constants.PROCESS_BECOME_PENDING_MESSAGE,
                                    cpuProcessId, CPU2Id));
                        } else {
                            shouldQueueGetPerformersBeNotified = false;
                            System.out.println(String.format(Constants.PROCESS_WAS_DELETED_MESSAGE, cpuProcessId));
                        }
                    }
                }
                if (shouldQueueGetPerformersBeNotified) {
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