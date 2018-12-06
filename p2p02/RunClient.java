import java.net.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class RunClient {
  private String[] hostName = null;
  private int port = 49700;
  private int j = 0;

  public RunClient(int j, int port, String[] hostName) {
    this.hostName = hostName;
    this.port = port;
    this.j = j;
  }


  public void clientRun() {

      for(int i = 0; i < hostName.length; i++) {
        if(!hostName[i].equals(SyncThread.localHost)) {
          try {
            System.out.println("Host: " + hostName[i]);
            Socket client = new Socket(hostName[i], port);

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("CLIENT: " + client.getLocalSocketAddress());
            j++;

            JSONParser parser = new JSONParser();
             JSONObject obj = new JSONObject();
             JSONArray array = new JSONArray();
             String chainId = "";
             int chainLength = 0;
             String lastBlockHash = "";
             Long lastBlockNumber = 0L;

             try {
               Object zolChain = parser.parse(new FileReader("18.json"));
               obj = (JSONObject)zolChain;
               chainId = (String)obj.get("Chain");
               array = (JSONArray)obj.get("Blocks");
               chainLength = array.size();
               Object lastBlock = array.get(chainLength - 1);
               JSONArray jsonLastBlock = (JSONArray)lastBlock;
               lastBlockNumber = (Long)jsonLastBlock.get(0);
               lastBlockHash = (String)jsonLastBlock.get(3);
             } catch(ParseException e) {
               e.printStackTrace();
             } catch(Exception e) {
               e.printStackTrace();
             }

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            String inMsg = in.readUTF();

            int lastBlockHashIndex = inMsg.indexOf("lastBlockHash");
            String inLastBlockHash = inMsg.substring(lastBlockHashIndex + 15, lastBlockHashIndex + 79);

            int lastBlockNumberIndex = inMsg.indexOf("lastBlockNumber");
            String inLastBlockNumber = inMsg.substring(lastBlockNumberIndex + 17);

            int chainIndex = inMsg.indexOf("chain");
            String inChainId = inMsg.substring(chainIndex + 7, chainIndex + 9);

            String chainState = "";
            Long inLastBlockNumberLong = Long.parseLong(inLastBlockNumber);
            int blockNumberState = lastBlockNumber.compareTo(inLastBlockNumberLong);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            int bytesRead = 0;
            byte[] aByte = new byte[1];

            try {
              fos = new FileOutputStream("from_server.json");
              bos = new BufferedOutputStream(fos);
              bytesRead = inFromServer.read(aByte, 0, aByte.length);

              do {
                baos.write(aByte);
                bytesRead = inFromServer.read(aByte);
              } while( bytesRead != -1);

              bos.write(baos.toByteArray());
              bos.flush();
              bos.close();
            } catch(IOException e) {
              System.out.println("IOException at receiving chain json" + e.getMessage());
            }

            if(blockNumberState == 0) {
              chainState = "Chain is good, you can mine";
              BlockchainFunc.blockchainfunction("18");
            } else if(blockNumberState < 0) {
              chainState = "You should synchronize your chain!";
            } else {
              chainState = "You have the longest chain, you can mine!";
              BlockchainFunc.blockchainfunction("18");
            }

            System.out.println("CLIENT lastBlockNumber: " + inLastBlockNumberLong +
            " lastBlockHash: " + inLastBlockHash + " chain: " + inChainId +
            "\nChainState: " + chainState + " lastBlockNumber: " + lastBlockNumber);

          } catch(Exception e) {
            System.out.println("Exception at Client: " + e.getMessage());
          }
        } else {
          System.out.println("Host: " + hostName[i] + " localHost: " + SyncThread.localHost);
        }
      }

  }
}
