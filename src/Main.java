public class Main {

    public static void main(String[] args) throws InterruptedException{
        int queueCapacity = 7;
        int procGenNum = 5;

        Monitor CPU1Monitor = new Monitor(1);
        Monitor CPU2Monitor = new Monitor(2);

        CPUQueue queue = new CPUQueue(queueCapacity);
        CPU CPU1 = new CPU(1, queue, CPU1Monitor);
        CPU CPU2 = new CPU(2, queue, CPU2Monitor);
        CPUProcessScheduler CPUProcessScheduler = new CPUProcessScheduler(queue, CPU1Monitor, CPU2Monitor);
        CPU1.setCpuProcessScheduler(CPUProcessScheduler);
        CPU2.setCpuProcessScheduler(CPUProcessScheduler);
        CPU1ProcessGenerator CPU1ProcessGenerator =
                new CPU1ProcessGenerator(1, procGenNum, CPUProcessScheduler, CPU1Monitor, CPU2Monitor, queue);
        CPU1ProcessGenerator CPU2ProcessGenerator =
                new CPU1ProcessGenerator(2, procGenNum, CPUProcessScheduler, CPU1Monitor, CPU2Monitor, queue);
        Thread CPU1Thread = new Thread(CPU1);
        Thread CPU2Thread = new Thread(CPU2);
        Thread.sleep(1000);
        Thread CPUProcessSchedulerThread = new Thread(CPUProcessScheduler);
        Thread CPU1ProcessGeneratorThread = new Thread(CPU1ProcessGenerator);
        Thread CPU2ProcessGeneratorThread = new Thread(CPU2ProcessGenerator);

        CPU1Thread.start();
        CPU2Thread.start();
        CPUProcessSchedulerThread.start();
        CPU1ProcessGeneratorThread.start();
        CPU2ProcessGeneratorThread.start();

        CPU1ProcessGeneratorThread.join();
        CPU2ProcessGeneratorThread.join();

    }

}
