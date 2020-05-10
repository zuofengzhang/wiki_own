/**
 * A、B两个线程轮流输出1~100的数字<br/>
 * A线程输出: 1、3、5...47、49、 52、54...98、100 <br/>
 * B线程输出: 2、4、6...48、50、51、53、55...97、99 两个线程输出个数相同
 */
public class RunningInTurnWithLock {

    static class Worker extends Thread {
        private final String name;
        private final Object thisSemaphore;
        private int value;

        public Worker(String name, Object thisSemaphore, int initialValue) {
            this.name = name;
            this.thisSemaphore = thisSemaphore;
            this.value = initialValue;
        }

        @Override
        public void run() {
            int cnt = 0;
            while (value <= 100) {
                try {

                    synchronized (thisSemaphore) {
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
                        thisSemaphore.notify();
                        if (value <= 100) {
                            thisSemaphore.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(name + " cnt : " + cnt);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object lock1 = new Object();

        Worker workerA = new Worker("a", lock1, 1);
        Worker workerB = new Worker("b", lock1, 2);

        workerA.start();
        workerB.start();
    }

}