package server;

import java.io.*;
import java.net.Socket;


public class ClientThread extends Thread {

    private Socket socket;

    private Server server;

    private PrintWriter writer;

    private boolean join;

    private String username;

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

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                Message message = new Message(this, line);
                if (message.getType() == 'l' || message.getType() == 's') {
                    server.send(new Message('l', this, "Message type is not supported"));
                } else if (join || (!join && message.getType() == 'u')) {
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

    public void send(String message) {
        writer.println(message);
        writer.flush();
    }

    public String getUsername() {
        return username;
    }

    public boolean setUsername(String username) {
        if (join)
            return false;

        this.username = username;
        join = true;

        return true;
    }

    public boolean isJoin() {
        return join;
    }
}
