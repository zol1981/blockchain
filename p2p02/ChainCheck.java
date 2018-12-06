import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.security.*;

public class ChainCheck {

  public void chckChainIntegrity(String fileName) throws IOException {
    String chainFile = fileName;

    JSONParser parser = new JSONParser();
    try {
      Object zolChain = parser.parse(new FileReader(chainFile));
      JSONObject obj = (JSONObject)zolChain;
      String chainId = (String)obj.get("Chain");
      JSONArray array = (JSONArray)obj.get("Blocks");
      int chainLength = array.size();
      Object block = array.get(0);
      JSONArray jsonBlock = (JSONArray)block;
      System.out.println("ChainId: " + chainId + " blockNumber: " + jsonBlock.get(0));
      System.out.println("Chain length: " + chainLength);

      Block[] checkBlock = new Block[chainLength];
      String[] msg = new String[chainLength];
      String[] hash = new String[chainLength];
      Block prevBlock = new Block(0L, 0L, "", "0000", "0000", 0L);

      for(int i = 0; i < chainLength; i++) {
        //Object curre
        JSONArray currentBlock = (JSONArray)array.get(i);
        checkBlock[i] = new Block((long)currentBlock.get(0), (long)currentBlock.get(1), (String)currentBlock.get(2),
        (String)currentBlock.get(3), (String)currentBlock.get(4), (long)currentBlock.get(5));

        if(i > 0) {
          msg[i] = "" + checkBlock[i].getBlockNumber() + " " + checkBlock[i].getBlockNonce() + " "
          + checkBlock[i].getBlockData() + " " + checkBlock[i].getPreviousHash()
          + " " + checkBlock[i].getBlockTimestamp();

          msg[i - 1] = "" + checkBlock[i - 1].getBlockNumber() + " " + checkBlock[i - 1].getBlockNonce() + " "
          + checkBlock[i - 1].getBlockData() + " " + checkBlock[i - 1].getPreviousHash()
          + " " + checkBlock[i - 1].getBlockTimestamp();

          try {
            hash[i] = Sha256.ShaMiner(msg[i]);
            hash[i - 1] = Sha256.ShaMiner(msg[i - 1]);
          } catch(Exception e) {
            System.out.println("Exception at ShaMiner: " + e);
          }

          String prevBlockHash = (String)checkBlock[i].getPreviousHash();

          if(i > 1) {
            if(!prevBlockHash.equals(hash[i - 1])) {
              System.out.println("Corrupted chain!!! Corruption at block: " + i);
              System.out.println(prevBlockHash);
              //System.out.println(prevBlockHash.getClass().getName());
              System.out.println(hash[i - 1]);
              //System.out.println(hash[i - 1].getClass().getName());
              i = chainLength;
            }
          }

        }

        if(i == 100) {
          System.out.println("100st block: " + checkBlock[i].getBlockNumber() + " " + checkBlock[i].getBlockNonce() + " "
          + checkBlock[i].getBlockData() + " " + checkBlock[i].getPreviousHash() + " " + checkBlock[i].getBlockHash()
          + " " + checkBlock[i].getBlockTimestamp());

          System.out.println("100st block: " + prevBlock.getBlockNumber() + " " + prevBlock.getBlockNonce() + " "
          + prevBlock.getBlockData() + " " + prevBlock.getPreviousHash() + " " + prevBlock.getBlockHash()
          + " " + prevBlock.getBlockTimestamp());

          System.out.println("------------------------------------------------");

          System.out.println("current msg: " + msg[i]);
          System.out.println("prev msg: " + msg[i - 1]);

          System.out.println("------------------------------------------------");

          System.out.println("current hash: " + hash[i]);
          System.out.println("prev hash: " + hash[i - 1]);
        }


        prevBlock = new Block((long)currentBlock.get(0), (long)currentBlock.get(1), (String)currentBlock.get(2),
        (String)currentBlock.get(3), (String)currentBlock.get(4), (long)currentBlock.get(5));
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
