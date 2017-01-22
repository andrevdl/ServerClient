package server;

import java.io.*;
import java.net.Socket;

/**
 * Handling the client on server side to send and receive data from the client.
 */
public class ClientThread extends Thread {

    /**
     * Socket of the client.
     */
    private Socket socket;

    /**
     * Reference of the server.
     */
    private Server server;

    /**
     * Writer to send data from the server to the client.
     */
    private PrintWriter writer;

    /**
     * Client is joined after setting his username.
     */
    private boolean join;

    /**
     * Username of the client.
     */
    private String username;

    /**
     * Create a new server side client.
     * @param socket Socket of the client.
     * @param server Reference of the server.
     */
    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

        try {
            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream);
        } catch (IOException e) {
            //
        }
    }

    /**
     * Handle the lifetime of the client.
     */
    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // parse received data from the client.
            String line;
            while ((line = reader.readLine()) != null) {
                Message message = new Message(this, line);

                // loopback and system messages are restricted by the server.
                if (message.getType() == 'l' || message.getType() == 's') {
                    server.send(new Message('l', this, "Message type is not supported"));
                }

                // messages other than username can only send after the username is set.
                else if (join || (!join && message.getType() == 'u')) {
                    if (!server.send(message)) {
                        server.send(new Message('l', this, "Message type is not supported"));
                    }
                } else {
                    server.send(new Message('l', this, "Username need to be set"));
                }
            }

            server.disconnect(this, false);
        } catch (IOException e) {
            server.disconnect(this, true);
        }
    }

    /**
     * Send a message to the client.
     * @param message Message to send.
     */
    public void send(String message) {
        writer.println(message);
        writer.flush();
    }

    /**
     * Get the username of the client.
     * @return the username of the client.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of the client.
     * @param username the username of the client.
     * @return Username could be set.
     */
    public boolean setUsername(String username) {
        if (join)
            return false;

        this.username = username;
        join = true;

        return true;
    }

    /**
     * Client has joined.
     * @return has joined.
     */
    public boolean isJoin() {
        return join;
    }
}
