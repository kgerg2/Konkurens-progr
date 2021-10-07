// import java.util.Random;

// public class Philosopher implements Runnable {
//     private Object leftFork;
//     private Object rightFork;
//     private int id;

//     public Philosopher(Object leftFork, Object rightFork, int id) {
//         this.leftFork = leftFork;
//         this.rightFork = rightFork;
//         this.id = id;
//     }

//     @Override
//     public void run() {
//         for (int i = 0; i < 1000; i++) {
//             eat();
//             think();
//         }
//     }

//     public void think() {
//         System.out.println("Philosopher " + id + " is thinking...");
//         Random random = new Random();
//         try {
//             Thread.sleep(random.nextInt(11));
//         } catch (InterruptedException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }

//     public void eat() {
//         Random random = new Random();
//         synchronized (leftFork) {
//             synchronized (rightFork) {
//                 System.out.println("Philosopher " + id + " is eating.");
//                 try {
//                     Thread.sleep(random.nextInt(11));
//                 } catch (InterruptedException e) {
//                     // TODO Auto-generated catch block
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }
// }
