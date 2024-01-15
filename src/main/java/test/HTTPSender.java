package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPSender {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://ljthey.co.uk:8000");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Setting the request method
            con.setRequestMethod("GET");

            // Reading the response
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Print the response
            System.out.println(content.toString());

            // Closing the connection
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
