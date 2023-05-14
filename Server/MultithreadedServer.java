import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.logging.*;

/**
 * MultithreadedServer é um servidor TCP/IP multithreaded com conexões limitadas por IP.
 * Ele utiliza SSL para conexões seguras e pode criar tarefas no Nomad.
 */
public class MultithreadedServer {

    private static int SERVER_PORT;
    private static String NOMAD_API_URL;
    private static final int MAX_THREADS = 50; // Maximum number of concurrent threads
    private static final int MAX_CONNECTIONS_PER_IP = 5;
    private static final Map<String, Integer> connectionCounts = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(MultithreadedServer.class.getName()); // Create a logger

    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true); // Logs will be written to application.log
            fileHandler.setFormatter(new SimpleFormatter()); // Set the log format
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO); // Set the minimum level of log messages to display
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Falha ao configurar o manipulador de arquivos de log", e);
        }
    }

    /**
     * O ponto de entrada do programa.
     *
     * @param args os argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Load properties from config file
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            SERVER_PORT = Integer.parseInt(prop.getProperty("server.port"));
            NOMAD_API_URL = prop.getProperty("nomad.api.url");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An error occurred while loading the configuration file: ", ex);
            return; // Stop execution if config can't be loaded
        }

        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Utilizando a SSLServerSocketFactory para criar um ServerSocket com SSL
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try (ServerSocket serverSocket = ssf.createServerSocket(SERVER_PORT)) {
            logger.log(Level.INFO, "Server is listening on port " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                InetAddress clientAddress = socket.getInetAddress();

                // Limitando o número de conexões por endereço IP
                int connections = connectionCounts.getOrDefault(clientAddress.getHostAddress(), 0);
                if (connections >= MAX_CONNECTIONS_PER_IP) {
                    logger.log(Level.WARNING, "Too many connections from " + clientAddress.getHostAddress());
                    socket.close();
                    continue;
                }

                connectionCounts.put(clientAddress.getHostAddress(), connections + 1);

                logger.log(Level.INFO, "New client connected: " + clientAddress.getHostAddress());
                ServerThread serverThread = new ServerThread(socket);
                executor.execute(serverThread);

                createNomadJob("new-task-" + System.currentTimeMillis());
            }

        } catch (BindException ex) {
            logger.log(Level.SEVERE, "The server port " + SERVER_PORT + " is already in use: ", ex);
        } catch (IOException ex)
        {
            logger.log(Level.SEVERE, "An error occurred while trying to create or use the server socket: ", ex);
        }

        executor.shutdown(); // Don't forget to shut down the executor when done
    }

    /**
     * ServerThread é uma classe interna que representa um thread de manipulação de cliente.
     */
    static class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        /**
         * O método run() é chamado quando o thread é iniciado.
         * Manipula a conexão do cliente.
         */
        public void run() {
            try (InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 OutputStream output = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(output, true)) {
            try {
                // Handle client connection...
            } finally {
                int connections = connectionCounts.get(socket.getInetAddress().getHostAddress());
                connectionCounts.put(socket.getInetAddress().getHostAddress(), connections - 1);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An error occurred while trying to handle client connection: ", ex);
        }
    }

    /**
     * Cria um trabalho no Nomad.
     *
     * @param jobName o nome do trabalho a ser criado
     */
    static void createNomadJob(String jobName) {
        try {
            HttpPost post = new HttpPost(NOMAD_API_URL);
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity("{\"job\": {\"id\": \"" + jobName + "\"}}"));
            HttpClients.createDefault().execute(post);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An error occurred while trying to create a Nomad job: ", ex);
        }
    }
}
}

