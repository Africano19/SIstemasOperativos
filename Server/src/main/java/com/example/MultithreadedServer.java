package com.example;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.*;

public class MultithreadedServer {
    private static final int MAX_THREADS = 50;

    public static void main(String[] args) {
        int port = 8080;
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Defina o caminho do seu keystore
        String keystorePath = "/Users/bolt_40n/Documents/GitHub/SIstemasOperativos/Server/keystore.jks";

        // Defina a senha do seu keystore
        String keystorePassword = "Q1w2E3.A4s5D6";

        try {
            // Carregue o keystore
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream fis = new FileInputStream(keystorePath)) {
                keystore.load(fis, keystorePassword.toCharArray());
            }

            // Crie a fábrica de gerenciadores de chaves
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, keystorePassword.toCharArray());

            // Crie o contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Crie a fábrica de sockets SSL
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Crie o socket do servidor
            try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port)) {
                System.out.println("Server is listening on port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    InetAddress clientAddress = socket.getInetAddress();
                    System.out.println("New client connected: " + clientAddress.getHostAddress());

                    executor.submit(new ServerThread(socket));
                }
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                executor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define a classe ServerThread, que é usada para lidar com as conexões do cliente
    static class ServerThread extends Thread {
        // Define o socket que será usado para se comunicar com o cliente
        private Socket socket;

        // Construtor para a classe ServerThread
        public ServerThread(Socket socket) {
            // Inicializa o socket
            this.socket = socket;
        }

        // Define o comportamento do thread quando é iniciado
        public void run() {
            try {
                // Obtém a entrada do socket
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // Obtém a saída do socket
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                // Lê a entrada do cliente linha por linha
                String line;
                while ((line = reader.readLine()) != null) {
                    // Se a linha está vazia, sai do loop
                    if (line.isEmpty()) {
                        break;
                    }

                    // Verifica se a linha começa com "GET" para identificar uma solicitação HTTP GET
                    if (line.startsWith("GET")) {
                        // Divide a linha em partes
                        String[] parts = line.split(" ");

                        // Obtém o arquivo solicitado
                        String requestedFile = parts[1].substring(1);

                        // Se nenhum arquivo foi especificado, usa "index.html"
                        if (requestedFile.isEmpty()) {
                            requestedFile = "index.html";
                        }

                        // Tenta obter o arquivo solicitado dos recursos do classpath
                        InputStream resourceStream = MultithreadedServer.class.getResourceAsStream("/" + requestedFile);

                        // Se o arquivo foi encontrado, envia-o de volta ao cliente
                        if (resourceStream != null) {
                        // Determina o tipo MIME do arquivo
                        String mimeType = URLConnection.guessContentTypeFromStream(resourceStream);
                        // Lê o conteúdo do arquivo
                        byte[] fileContent = resourceStream.readAllBytes();

                        // Envia uma resposta HTTP 200 OK ao cliente
                        sendHttpResponse(writer, output, "HTTP/1.1 200 OK", mimeType, fileContent);
                    } else {
                        // Se o arquivo não foi encontrado, envia uma resposta HTTP 404 Not Found
                        sendHttpResponse(writer, output, "HTTP/1.1 404 Not Found", "text/html", null);
                    }
                }
            }

            // Fecha o socket
            socket.close();

        } catch (IOException ex) {
            // Imprime qualquer erro que ocorrer com o thread
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendHttpResponse(PrintWriter writer, OutputStream output, String status, String contentType, byte[] content) throws IOException {
        writer.println(status);
        writer.println("Content-Type: " + contentType);
        if (content != null) {
            writer.println("Content-Length: " + content.length);
            writer.println();
            output.write(content);
        } else {
            writer.println("Content-Length: 0");
            writer.println();
        }
        output.flush();
    }
}
}

                        
