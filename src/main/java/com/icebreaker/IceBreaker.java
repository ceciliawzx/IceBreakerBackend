package com.icebreaker;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class IceBreaker {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!!!");
        // Create an HTTP server listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Create a context for the root URI ("/")
        server.createContext("/", new MyHandler());

        // Start the server
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Hello!!!");
            // Print request body
            System.out.println("Request Body: ");
            InputStream requestBody = exchange.getRequestBody();
            String body = new Scanner(requestBody, StandardCharsets.UTF_8.name()).useDelimiter("\\A").next();
            System.out.println(body);
            String response = "Hello, this is the response";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
