// Define o pacote
package com.example;

// Importa as bibliotecas Java necessárias
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Define a classe principal
public class MultithreadedServer {

    // Define o número máximo de threads que podem ser usados para lidar com conexões de cliente
    private static final int MAX_THREADS = 50;

    // Crie uma instância do logger
    private static final Logger logger = LogManager.getLogger(MultithreadedServer.class);

    // Método principal, que é o ponto de entrada do programa
    public static void main(String[] args) throws IOException {
        // Define o número da porta
        int port = 8080;

    // Cria um ExecutorService com um número fixo de threads
    ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Tenta criar um socket de servidor, que escuta por conexões TCP recebidas
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server is listening on port " + port);

            // Informa que o servidor foi iniciado e está escutando na porta especificada
            System.out.println("Server is listening on port " + port);

            // Entra em um loop infinito para aceitar conexões contínuas
            while (true) {
                // Aceita a conexão do cliente
                Socket socket = serverSocket.accept();

                // Obtém o endereço do cliente
                InetAddress clientAddress = socket.getInetAddress();

                // Informa que um novo cliente foi conectado
                logger.info("New client connected: " + clientAddress.getHostAddress());

                // Submete a nova tarefa ao executor
                executor.submit(new ServerThread(socket));

                // Cria um novo thread do servidor para lidar com a conexão do cliente e o inicia
//                new ServerThread(socket).start();
                executor.submit(new ServerThread(socket));
            }
        } catch (IOException ex) {

            logger.error("Server exception: " + ex.getMessage(), ex);

        } finally {
            executor.shutdown();
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

                        
