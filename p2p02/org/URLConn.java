package json_simple.src.main.java;

import java.net.*;
import  java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONException;
import org.json.simple.JSONObject;

public class URLConn {
  public static void main(String args[]) {
    try {
      URL url = new URL("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest");
      URLConnection urlConnection = url.openConnection();
      HttpURLConnection connection = null;
      if(urlConnection instanceof HttpURLConnection) {
        connection = (HttpURLConnection) urlConnection;
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("path", "");
        connection.setRequestProperty("X-CMC_PRO_API_KEY", "10470418-49ed-481f-8f44-913f827ce34e");
      } else {
        System.out.println("Please enter an HTTP URL");
        return;
      }

      BufferedReader in = new BufferedReader(
      new InputStreamReader(connection.getInputStream()));
      String urlString = "";
      String current;

      while ((current = in.readLine()) != null) {
        urlString += current;
      }

      JSONObject coins = new JSONObject(urlString);
      JSONArray coin = coins.getJSONArray("data");

      System.out.println(coin);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
