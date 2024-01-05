import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The {@code ClientHandler} class implements the {@code Runnable} interface and represents a thread
 * that manages communication with a specific client. It is responsible for broadcasting messages
 * to all connected clients.
 */
public class ClientHandler implements Runnable {
    //ArrayList that contains all the active clients
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    /**
     * Initializes the {@code ClientHandler} with the provided socket and adds the client
     * to the list of active clients.
     *
     * @param socket The socket associated with the client.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = bufferedReader.readLine();
            clients.add(this);
            broadcastMessage("\n=======================================\n" +
                    "SERVER MESSAGE: " + clientName + " has entered the chat!\n" +
                    "=======================================\n");
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    /**
     * Overrides the {@code run} method of the {@code Runnable} interface. Listens for incoming messages
     * from the associated client and broadcasts them to all connected clients.
     */
    @Override
    public void run() {
        String message;

        while(socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                broadcastMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     *
     * @param message The message to be broadcast.
     */
    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
        }
    }

    /**
     * Removes the client handler from the list of active clients and broadcasts a message
     * about the client leaving.
     */
    public void removeClientHandler() {
        clients.remove(this);
        broadcastMessage("\n=======================================\n" +
                "/!\\ SERVER MESSAGE: " + clientName + " left the chat!\n" +
                "=======================================\n");
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
        removeClientHandler();
        try {
            if (socket != null)
                socket.close();

            if (reader != null)
                reader.close();

            if (writer != null)
                writer.close();

        } catch (IOException e) { e.printStackTrace(); }
    }
}