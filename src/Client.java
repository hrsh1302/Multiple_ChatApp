import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * The {@code Client} class represents a simple client that connects to the server using sockets.
 * It includes methods to send and receive messages to and from the server.
 */
public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    /**
     * Constructor of the {@code Client} class with the provided socket and username.
     *
     * @param socket   The socket for communication with the server.
     * @param username The username associated with the client.
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) { closeEverything(socket, reader, writer); }
    }

    /**
     * Sends messages from the client to the server.
     */
    public void sendMessage() {
        try {
            writer.write(username);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                writer.write("\t" + username + ": " + messageToSend);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) { closeEverything(socket, reader, writer); }
    }

    /**
     * Listens for incoming messages from the server.
     */
    public void listenForMessage() {
        new Thread(() -> {
            String messageGroupChat;

            while (socket.isConnected()) {
                try {
                    messageGroupChat = reader.readLine();
                    System.out.println(messageGroupChat);
                } catch (IOException e) { closeEverything(socket, reader, writer); }
            }
        }).start();
    }

    /**
     * Closes the socket, reader, and writer associated with the client handler,
     * removing the client from the list of active clients.
     *
     * @param socket The socket associated with the client handler.
     * @param reader The reader associated with the client handler.
     * @param writer The writer associated with the client handler.
     */
    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (socket != null)
                socket.close();

            if (reader != null)
                reader.close();

            if (writer != null)
                writer.close();

        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
