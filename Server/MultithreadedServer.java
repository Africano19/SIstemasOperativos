import java.io.*;
import java.net.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class MultithreadedServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                InetAddress clientAddress = socket.getInetAddress();
                System.out.println("New client connected: " + clientAddress.getHostAddress());
                new ServerThread(socket).start();
                createNomadJob("multithreadserver-" + System.currentTimeMillis());
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void createNomadJob(String jobName) {
        String nomadApiUrl = "http://localhost:4646/v1/jobs";
    
        String jobHCL = "job \"" + jobName + "\" {\n" +
                        "  datacenters = [\"dc1\"]\n" +
                        "  type        = \"service\"\n" +
                        "\n" +
                        "  group \"serverGroup\" {\n" +
                        "    count = 1\n" +
                        "\n" +
                        "    task \"serverTask\" {\n" +
                        "      driver = \"raw_exec\"\n" +
                        "\n" +
                        "      config {\n" +
                        "        command = \"java\"\n" +
                        "        args    = [\"-Xms128M\", \"-Xmx256M\", \"-jar\", \"local/multithreaded-server-1.0.0.jar\"]\n" +
                        "      }\n" +
                        "\n" +
                        "      resources {\n" +
                        "        cpu    = 500\n" +
                        "        memory = 256\n" +
                        "\n" +
                        "        network {\n" +
                        "          mbits = 10\n" +
                        "          port \"http\" {\n" +
                        "            static = 8080\n" +
                        "          }\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}\n";
    
        try {
            HttpPost request = new HttpPost(nomadApiUrl);
            request.setEntity(new StringEntity(jobHCL));
            request.setHeader("Content-Type", "text/plain");
    
            HttpClients.createDefault().execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ServerThread extends Thread {
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

                        InputStream resourceStream = getClass().getResourceAsStream("/resources/" + requestedFile);
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
}
