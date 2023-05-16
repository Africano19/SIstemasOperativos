package com.example;

import java.io.*;
import java.net.*;

public class TestClient {
    private static final int NUM_REQUESTS = 100;

    public static void main(String[] args) throws Exception {
        // Inicia várias threads que fazem solicitações para o servidor simultaneamente
        for (int i = 0; i < NUM_REQUESTS; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Abre uma conexão com o servidor
                        Socket socket = new Socket("localhost", 8080);

                        // Envia uma solicitação GET
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("GET / HTTP/1.1");
                        out.println("Host: localhost");
                        out.println();

                        // Lê a resposta do servidor
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = in.readLine()) != null) {
                            System.out.println(line);
                        }

                        // Fecha a conexão
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
