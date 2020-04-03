package HttpLib.protocol;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SelectiveRepeatRegistryTest {

    @org.junit.jupiter.api.Test
    void threadSafe() throws InterruptedException {
        // Not robust, simply a general check to ensure that nothing blocks within a given time...
        SelectiveRepeatRegistry sr = new SelectiveRepeatRegistry((short) 10);

        // Test threads
        Runnable testSRR = () -> {
            // each thread will try to send 4 packet .. kinda
            int count = 0;
            while (count < 4) {

                while (!sr.canSend()) {
                    System.out.println(Thread.currentThread()+" | Window full. Waiting for simulation...");
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Query for next
                int seqNumber = sr.requestNextSeqNumber();
                System.out.println(Thread.currentThread()+" | Got number: " + seqNumber);

                // Simulate RTT
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // .. when we receive ack...
                sr.confirm(seqNumber);

                count++;
            }
        };

        // Test procedure with concurrency
        int tCount = 11;
        ArrayList<Thread> threads = new ArrayList<>(tCount);
        for (int i = 0; i < tCount; i++) {
            Thread t = new Thread(testSRR);
            t.start();
            threads.add(t);
        }

        // Everything should conclude in time
        for (int i = 0; i < tCount; i++) {
            threads.get(i).join(1000);
            assertFalse(threads.get(i).isAlive());
        }

    }

    @org.junit.jupiter.api.Test
    void canSend() {
    }

    @org.junit.jupiter.api.Test
    void requestNextSeqNumber() {
    }

    @org.junit.jupiter.api.Test
    void inWindow() {
    }
}