import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The {@code Server} class represents a simple server that listens for incoming client connections
 * and creates a separate thread for each connected client using the {@code ClientHandler} class.
 */
public class Server {
    private ServerSocket serverSocket;

    /**
     * Constructor of the {@code Server} class with provided socket
     * @param socket The socket for communication with the server.
     */
    public Server(ServerSocket socket) {
        this.serverSocket = socket;
    }

    /**
     * Listens for incoming client connections and spawns a new {@code ClientHandler} thread
     * for each connected client.
     */
    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client is connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Closes the server socket.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();

        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
