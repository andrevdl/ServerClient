package client;

import java.io.IOException;
import java.net.Socket;

/**
 * Client.
 */
public class Client {

    /**
     * Port to the server.
     */
    private static final int SERVER_PORT = 4000;

    /**
     * Address of the server.
     */
    private static final String SERVER_ADDRESS = "localhost";

    /**
     * Backbone of a client.
     * @param args Arguments.
     */
    public static void main(String[] args) {
        Socket socket;

        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            Reader reader = new Reader(socket);
            reader.start();

            Writer writer = new Writer(socket);
            writer.start();
        } catch (IOException e) {
            //
        }
    }
}
