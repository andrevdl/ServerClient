package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Read the received messages from the server and write it to the console.
 */
public class Reader extends Thread {

    /**
     * Input stream.
     */
    private InputStream inputStream;

    /**
     * Constructor.
     * @param socket Socket the server.
     * @throws IOException Exception.
     */
    public Reader(Socket socket) throws IOException {
        inputStream = socket.getInputStream();
    }

    /**
     * Write all received data to the console.
     */
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            //
        }

        System.out.println("Server is offline");
    }
}
