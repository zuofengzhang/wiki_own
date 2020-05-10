import java.util.concurrent.Semaphore;

/**
 * A、B两个线程轮流输出1~100的数字<br/>
 * A线程输出: 1、3、5...47、49、    52、54...98、100 <br/>
 * B线程输出: 2、4、6...48、50、51、53、55...97、99 两个线程输出个数相同
 */
public class RunningInTurnWithSemaphore {

    static class Worker extends Thread {
        private final String name;
        private final Semaphore thisSemaphore;
        private final Semaphore nextSemaphore;
        private int value;

        public Worker(String name, Semaphore thisSemaphore, Semaphore nextSemaphore, int initialValue) {
            this.name = name;
            this.thisSemaphore = thisSemaphore;
            this.nextSemaphore = nextSemaphore;
            this.value = initialValue;
        }

        @Override
        public void run() {
            int cnt = 0;
            while (value <= 100) {
                try {
                    thisSemaphore.acquire();
                    System.out.println(name + ":\t" + value);
                    cnt++;
                    if (value == 50) {
                        value = 51;
                        System.out.println(name + ":\t" + value);
                        cnt++;
                        value += 2;
                    } else if (value == 49) {
                        value = 52;
                    } else {
                        value += 2;
                    }
                    nextSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(name + " cnt : " + cnt);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Semaphore aSemaphore = new Semaphore(1);
        Semaphore bSemaphore = new Semaphore(1);
        Worker workerA = new Worker("a", aSemaphore, bSemaphore, 1);
        Worker workerB = new Worker("b", bSemaphore, aSemaphore, 2);
        bSemaphore.acquire();
        workerA.start();
        workerB.start();
    }

}