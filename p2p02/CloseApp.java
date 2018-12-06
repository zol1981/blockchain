import java.util.*;

public class CloseApp extends Thread {
  private int i = 0;
  public void run() {

    while(SyncThread.appFlag) {
      try {
        System.out.println("Quit: q + Enter");
        Scanner scan = new Scanner(System.in);
        if(scan.hasNext("q")) {
          SyncThread.appFlag = false;
        } else {
          System.out.println("Quit: q + Enter");
        }
        i++;
        System.out.println("Close: " + i);
        //Thread.sleep(1000);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
}
