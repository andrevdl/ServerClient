package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Reader extends Thread {

    private InputStream inputStream;

    public Reader(Socket socket) throws IOException {
        inputStream = socket.getInputStream();
    }

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
