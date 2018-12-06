import java.security.MessageDigest;
import java.util.Date;
import  java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;


public class BlockchainFunc {
  private static Block[] blocks = new Block[1000];

  public static void blockchainfunction(String args) throws IOException {

    String chain = args;
    String chainFile = args + ".json";
    boolean goodChain = false;
    boolean writeFile = false;

    int chainLength = 0;

    JSONParser parser = new JSONParser();
    long lastBlockNumber = 0L;
    long lastBlockNonce = 0L;
    String lastBlockData = "";
    String lastBlockHash = "";
    String lastPrevHash = "";
    long lastBlockTimestamp = 0L;

    JSONObject jsonObject = new JSONObject();
    JSONArray blockArray = new JSONArray();

    try {
      Object zolChain = parser.parse(new FileReader(chainFile));
      jsonObject = (JSONObject)zolChain;
      String chainId = (String)jsonObject.get("Chain");
      //System.out.println("Chain id: " + chainId);
      if(chain.equals(chainId)) {
        goodChain = true;
      }
      blockArray = (JSONArray)jsonObject.get("Blocks");
      chainLength = blockArray.size();
      Object myBlock = blockArray.get(chainLength - 1);
      JSONArray myJSONBlock = (JSONArray)myBlock;
      lastBlockNumber = (long)myJSONBlock.get(0);
      lastBlockNonce = (long)myJSONBlock.get(1);
      lastBlockData = (String)myJSONBlock.get(2);
      lastBlockHash = (String)myJSONBlock.get(3);
      lastPrevHash = (String)myJSONBlock.get(4);
      lastBlockTimestamp = (long)myJSONBlock.get(5);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Block lastBlock = new Block(lastBlockNumber, lastBlockNonce, lastBlockData, lastBlockHash, lastPrevHash, lastBlockTimestamp);

    String prev;
    String hash = "";

    if(chainLength == 0) {
      prev = "00";
    } else {
      prev = lastBlock.getBlockHash();
    }

    String msg = "";

    Date date = new Date();
      long nonce = 0L;
      if(goodChain && prev.substring(0,2).equals("00")) {
        writeFile = true;
        do {
          msg = "" + chainLength + " " + nonce + " " + "maci" + chainLength + " " + prev + " " +  date.getTime();

          //System.out.println("msg: " + msg[i]);

          try {
              hash = Sha256.ShaMiner(msg);
          } catch (Exception e) {
              System.out.println("ShaMiner Exception: " + e);
          }

          nonce++;
        } while(!hash.substring(0,2).equals("00"));
      } else if(!goodChain){
        System.out.println("Wrong chain");
      } else {
        System.out.println("Corrupted chain");
      }

      blocks[chainLength] = new Block((long)chainLength, nonce - 1, "maci" + chainLength, hash, prev, date.getTime());

      JSONArray block = new JSONArray();
      block.add(blocks[chainLength].getBlockNumber());
      block.add(blocks[chainLength].getBlockNonce());
      block.add(blocks[chainLength].getBlockData());
      block.add(blocks[chainLength].getBlockHash());
      block.add(blocks[chainLength].getPreviousHash());
      block.add(blocks[chainLength].getBlockTimestamp());

      blockArray.add(block);


    jsonObject.put("Blocks", blockArray);

    if(writeFile) {
      FileWriter file = new FileWriter(chainFile);
      try {
        file.write(jsonObject.toJSONString());
        //System.out.println("Succes by FileWriter");
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        file.flush();
        file.close();
      }
    }
  }
}
