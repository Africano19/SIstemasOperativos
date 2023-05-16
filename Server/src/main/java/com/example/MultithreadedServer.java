// Define o pacote
package com.example;

// Importa as bibliotecas Java necessárias
import java.io.*;
import java.net.*;

// Define a classe principal
public class MultithreadedServer {

    // Método principal, que é o ponto de entrada do programa
    public static void main(String[] args) throws IOException {
        // Define o número da porta
        int port = 8080;

        // Tenta criar um socket de servidor, que escuta por conexões TCP recebidas
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Informa que o servidor foi iniciado e está escutando na porta especificada
            System.out.println("Server is listening on port " + port);

            // Entra em um loop infinito para aceitar conexões contínuas
            while (true) {
                // Aceita a conexão do cliente
                Socket socket = serverSocket.accept();
                
                // Obtém o endereço do cliente
                InetAddress clientAddress = socket.getInetAddress();
                
                // Informa que um novo cliente foi conectado
                
                System.out.println("New client connected: " + clientAddress.getHostAddress());
                
                // Cria um novo thread do servidor para lidar com a conexão do cliente e o inicia
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            // Imprime qualquer erro que ocorrer com o servidor
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
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
                                                        writer.println("HTTP/1.1 200 OK");
                                                        // Define o tipo de conteúdo da resposta
                                                        writer.println("Content-Type: " + mimeType);
                                                        // Define o tamanho do conteúdo da resposta
                                                        writer.println("Content-Length: " + fileContent.length);
                                                        // Envia uma linha vazia para indicar o fim dos cabeçalhos da resposta
                                                        writer.println();
                            
                                                        // Envia o conteúdo do arquivo
                                                        output.write(fileContent);
                                                        // Limpa o fluxo de saída
                                                        output.flush();
                                                    } else {
                                                        // Se o arquivo não foi encontrado, envia uma resposta HTTP 404 Not Found
                                                        writer.println("HTTP/1.1 404 Not Found");
                                                        // Define o tipo de conteúdo da resposta
                                                        writer.println("Content-Type: text/html");
                                                        // Define que o tamanho do conteúdo da resposta é 0
                                                        writer.println("Content-Length: 0");
                                                        // Envia uma linha vazia para indicar o fim dos cabeçalhos da resposta
                                                        writer.println();
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
                                }
                            }
                            