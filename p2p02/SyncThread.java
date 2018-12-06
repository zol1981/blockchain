import java.io.*;
import java.net.*;

class T1 extends Thread {
   SynchronizedThreads m;

   public T1(SynchronizedThreads m1) {
      this.m = m1;
      m.setServerPort(49700);
   }

   public void run() {
     while (SyncThread.appFlag) {
       m.runServer();
     }
   }
}

class T2 extends Thread {
   SynchronizedThreads m;
   String[] hostName = {"192.168.1.124", "192.168.1.134", "192.168.1.126"};

   public T2(SynchronizedThreads m2) {
      this.m = m2;
      m.setClient(0, 49700, hostName);
   }

   public void run() {
     int i = 0;
     while(SyncThread.appFlag) {
       m.runClient(5300);
       i++;
     }
   }
}

public class SyncThread {
  static boolean appFlag = true;
  static String localHost = "";
   public static void main(String[] args) {
      SynchronizedThreads m = new SynchronizedThreads();

      try(final DatagramSocket socket = new DatagramSocket()){
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        localHost = socket.getLocalAddress().getHostAddress();
      } catch(Exception e) {
        e.printStackTrace();
      }

      System.out.println("My IP: " + localHost);

      Thread t1 = new T1(m);
      Thread t2 = new T2(m);
      Thread t3 = new CloseApp();

      t1.start();
      t2.start();
      t3.start();
   }
}
