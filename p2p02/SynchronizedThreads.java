import java.io.*;
import java.net.*;

public class SynchronizedThreads {
   boolean flag = false;
   RunServer synchronizedServer;
   RunClient synchronizedClient;

   public void setServerPort(int port) {
     try {
       synchronizedServer = new RunServer(port);
     } catch(IOException e) {
       e.printStackTrace();
     }
   }

   public void setClient(int j, int port, String[] hostName) {
     synchronizedClient = new RunClient(j, port, hostName);
   }

   public synchronized void runServer() {
      if (flag) {
         try {
            wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      synchronizedServer.serverRun();
      flag = true;
      notify();
   }

   public synchronized void runClient(int port) {
      if (!flag) {
         try {
            wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      synchronizedClient.clientRun();
      flag = false;
      notify();
   }
}
