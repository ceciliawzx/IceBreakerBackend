package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPSender {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://ljthey.co.uk:8000");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Setting the request method to POST
            con.setRequestMethod("POST");

            // Enabling both input and output streams
            con.setDoOutput(true);

            // Custom message to send in the request body
            String message = "This is a message from Leo Li";

            // Sending the request
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = message.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Reading the response code
            int status = con.getResponseCode();
            System.out.println("Response Code: " + status);

            // Reading the response message
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Message: " + response.toString());
            }

            // Closing the connection
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
