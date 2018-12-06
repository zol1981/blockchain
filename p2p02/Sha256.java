import java.security.*;

public class Sha256 {
  public static String ShaMiner(String message) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(message.getBytes());
    byte[] digest = md.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < digest.length; i++) {
      String d = Integer.toHexString(0xFF &digest[i]);

      if(digest[i] >= 0 && digest[i] < 16) {
        d = "0" + d;
      }
      hexString.append(d);
    }
    //System.out.println("shaHash: " + hexString);
    return hexString.toString();
  }
}
