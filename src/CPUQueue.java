
import java.util.LinkedList;
import java.util.Queue;

public class CPUQueue {
    private final Queue<CPUProcess> queue = new LinkedList<>();
    private final CPUProcess CPUProcessNotForProcessing = new CPUProcess(0, "0-0");
    private int capacity;
    private int maxSize = 8;
    private boolean freeAllGetPerformersFlag;

    public CPUQueue(int capacity) {
        this.capacity = capacity;
        this.freeAllGetPerformersFlag = false;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized void put(CPUProcess element) throws InterruptedException {
        while(queue.size() == capacity || freeAllGetPerformersFlag) {
            System.out.println("Queue is FULL, waiting..");
            wait();
        }
        queue.add(element);

        if(queue.size()>maxSize)
            maxSize=queue.size();

        System.out.println("Process added, queue size = [" + queue.size() + "]\n");
        notifyAll(); // notifyAll() for multiple CPU/CPUProcess threads
    }

    public synchronized CPUProcess get() throws InterruptedException {
        CPUProcess item;
        while(queue.isEmpty() && !freeAllGetPerformersFlag) {
            System.out.println("Queue is EMPTY, waiting..");
            wait();
        }
        if (!queue.isEmpty()) {
            item = queue.remove();
            System.out.println("Process removed, queue size = [" + queue.size() + "]");
        } else {
            item = CPUProcessNotForProcessing;
        }
        freeAllGetPerformersFlag = false;
        notifyAll(); // notifyAll() for multiple CPU/CPUProcess threads
        return item;
    }

    public synchronized void notifyAllPerformers() {
        notifyAll();
    }

    public synchronized void notifyAllGetPerformers () {
        setFreeAllGetPerformersFlag(true);
        notifyAll();
    }

    public void setFreeAllGetPerformersFlag(boolean freeAllGetPerformersFlag) {
        this.freeAllGetPerformersFlag = freeAllGetPerformersFlag;
    }
}
