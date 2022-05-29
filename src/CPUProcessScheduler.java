import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CPUProcessScheduler implements Runnable{
    private final int RAND_MIN = 30;
    private final int RAND_MAX = 100;

    private final CPUQueue queue;
    private final Monitor CPU1Monitor;
    private final Monitor CPU2Monitor;

    private final Map<Integer, CPUProcess> pendingProcessesForCPUsMap;
    private final Random random = new Random();
    private final Map<Integer, Integer> currentProcessTypeForCPUMap; // 0 - not occupied or occcupied by procId = num > 0

    public CPUProcessScheduler(CPUQueue queue, Monitor CPU1Monitor, Monitor CPU2Monitor) {
        this.queue = queue;
        this.CPU1Monitor = CPU1Monitor;
        this.CPU2Monitor = CPU2Monitor;
        pendingProcessesForCPUsMap = new HashMap<>();
        currentProcessTypeForCPUMap = new HashMap<>();
        currentProcessTypeForCPUMap.put(CPU1Monitor.getId(), 0);
        currentProcessTypeForCPUMap.put(CPU2Monitor.getId(), 0);
    }

    @Override
    public void run() {
        schedule();
    }

    public void schedule() {
        int CPU2SchedulingCycleTime;
        int CPU1SchedulingCycleTime;
        int CPU1Id = CPU1Monitor.getId();
        int CPU2Id = CPU2Monitor.getId();
        CPUProcess pendingProcessForCPU1 = null;
        CPUProcess pendingProcessForCPU2 = null;
        boolean shouldProcOfType2BePutToQueue = false;
        boolean shouldQueueGetPerformersBeNotified = false;
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                queue.notifyAllPerformers();
                System.out.println("Executing scheduling for CPU" + CPU2Id);
                CPU2SchedulingCycleTime = RAND_MIN + random.nextInt(RAND_MAX);
                Thread.sleep(CPU2SchedulingCycleTime);
                synchronized (CPU2Monitor) {
                    pendingProcessForCPU2 = getPendingProcessForCPU(CPU2Id);
                    if (pendingProcessForCPU2 != null) {
                        shouldProcOfType2BePutToQueue = true;
                        setPendingProcessForCPU(CPU2Id, null);
                    }
                }
                if (shouldProcOfType2BePutToQueue) {
                    queue.put(pendingProcessForCPU2);
                    System.out.println(String.format(Constants.PROCESS_ADDED_TO_QUEUE_MESSAGE,
                            pendingProcessForCPU2.getId()));
                }
                shouldProcOfType2BePutToQueue = false;
                System.out.println(String.format("Scheduling for CPU %d has been executed.", CPU2Id));

                System.out.println("Executing scheduling for CPU" + CPU1Id);
                CPU1SchedulingCycleTime = RAND_MIN + random.nextInt(RAND_MAX);
                Thread.sleep(CPU1SchedulingCycleTime);
                synchronized (CPU1Monitor) {
                    synchronized (CPU2Monitor) {
                        pendingProcessForCPU1 = getPendingProcessForCPU(CPU1Id);
                        pendingProcessForCPU2 = getPendingProcessForCPU(CPU2Id);
                        if (pendingProcessForCPU1 != null &&
                                getCurrentProcessTypeForCPU(CPU1Id) == 1 &&
                                pendingProcessForCPU2 == null) {
                            setPendingProcessForCPU(CPU2Id, pendingProcessForCPU1);
                            setPendingProcessForCPU(CPU1Id, null);
                            shouldQueueGetPerformersBeNotified = true;
                            System.out.println(String.format(Constants.PROCESS_ADDED_TO_QUEUE_MESSAGE,
                                    pendingProcessForCPU1.getId()));
                        } else {
                            shouldProcOfType2BePutToQueue = false;
                            if (pendingProcessForCPU1 != null) {
                                System.out.println(String.format(Constants.PROCESS_WAS_DELETED_MESSAGE,
                                        pendingProcessForCPU1.getId()));
                            }
                        }
                    }
                }
                if (shouldQueueGetPerformersBeNotified) {
                    queue.notifyAllGetPerformers();
                }
                System.out.println(String.format("Scheduling for CPU %d has been executed.", CPU1Id));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }


    }

    public CPUQueue getQueue() {
        return queue;
    }

    /*public boolean isCPUFree(long id) {
        return CPUOccupiedByProcessMap.get(id) == 0;
    }*/

    public Integer getCurrentProcessTypeForCPU(int id) {
        return currentProcessTypeForCPUMap.get(id);
    }

    public synchronized void setCurrentProcessTypeForCPU(int cpuId, int processType) {
        currentProcessTypeForCPUMap.put(cpuId, processType);
    }

    public CPUProcess getPendingProcessForCPU(int id) {
        return pendingProcessesForCPUsMap.get(id);
    }

    public void setPendingProcessForCPU(int id, CPUProcess cpuProcess) {
        pendingProcessesForCPUsMap.put(id, cpuProcess);
    }
}
