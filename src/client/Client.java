package client;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private static final int SERVER_PORT = 4000;

    private static final String SERVER_ADDRESS = "localhost";

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
