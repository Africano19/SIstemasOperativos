import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class MultithreadedServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("GET")) {
                    String[] parts = line.split(" ");
                    String requestedFile = parts[1].substring(1);

                    if (requestedFile.isEmpty()) {
                        requestedFile = "index.html";
                    }

                    InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("resources/" + requestedFile);
                    if (resourceStream != null) {
                        String mimeType = URLConnection.guessContentTypeFromStream(resourceStream);
                        byte[] fileContent = resourceStream.readAllBytes();

                        writer.println("HTTP/1.1 200 OK");
                        writer.println("Content-Type: " + mimeType);
                        writer.println("Content-Length: " + fileContent.length);
                        writer.println();

                        output.write(fileContent);
                        output.flush();
                    } else {
                        writer.println("HTTP/1.1 404 Not Found");
                        writer.println("Content-Type: text/html");
                        writer.println("Content-Length: 0");
                        writer.println();
                    }
                }
            }

            socket.close();

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

