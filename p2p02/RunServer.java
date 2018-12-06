import java.net.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class RunServer {
  private ServerSocket serverSocket;

  public RunServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(2000);
  }

  public void serverRun() {
    try {
      System.out.println("Server running at port: " + serverSocket.getLocalPort());

      Socket server = serverSocket.accept();
      DataInputStream in = new DataInputStream(server.getInputStream());
      System.out.println("SERVER input: " + in.readUTF());

      JSONParser parser = new JSONParser();
       JSONObject obj = new JSONObject();
       JSONArray array = new JSONArray();
       String chainId = "";
       int chainLength = 0;
       long lastBlockNumber = 0L;
       String lastBlockHash = "";

       try {
         Object zolChain = parser.parse(new FileReader("18.json"));
         obj = (JSONObject)zolChain;
         chainId = (String)obj.get("Chain");
         array = (JSONArray)obj.get("Blocks");
         chainLength = array.size();
         Object lastBlock = array.get(chainLength - 1);
         JSONArray jsonLastBlock = (JSONArray)lastBlock;
         lastBlockNumber = (long)jsonLastBlock.get(0);
         lastBlockHash = (String)jsonLastBlock.get(3);
       } catch(ParseException e) {
         e.printStackTrace();
       } catch(Exception e) {
         e.printStackTrace();
       }

       DataOutputStream out = new DataOutputStream(server.getOutputStream());
       out.writeUTF("SERVER " + SyncThread.localHost + " chain: " + chainId + " lastBlockHash: "    // ezzel ment
        + lastBlockHash + " lastBlockNumber: " + lastBlockNumber);

      BufferedOutputStream outToClient = new BufferedOutputStream(server.getOutputStream());
      File fileToClient = new File("18.json");
      byte[] fileLength = new byte[(int)fileToClient.length()];
      FileInputStream fis = null;

      try{
        fis = new FileInputStream(fileToClient);
      } catch(FileNotFoundException e) {
        System.out.println("FileNotFoundException at send chain json" + e.getMessage());
      }

      BufferedInputStream bis = new BufferedInputStream(fis);

      try {
        bis.read(fileLength, 0, fileLength.length);
        outToClient.write(fileLength, 0, fileLength.length);
        outToClient.flush();
        outToClient.close();
      } catch(IOException e) {
        System.out.println("IOException at send chain json" + e.getMessage());
      }


    } catch(SocketTimeoutException e) {
      System.out.println("Server SocketTimeoutException: " + e.getMessage());
    } catch(IOException e) {
      System.out.println("Server IOException: " + e.getMessage());
    }
  }
}
