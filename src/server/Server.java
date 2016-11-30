package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Server {

    private static final int SERVER_PORT = 4000;

    private HashMap<String, ClientThread> clients;

    private ConsoleMessage cm;

    public static void main(String[] args) {
        new Server().run();
    }

    public void run() {
        ServerSocket serverSocket;
        clients = new HashMap<>();
        cm = new ConsoleMessage();

        try {
            serverSocket = new ServerSocket(SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                cm.connect();

                ClientThread thread = new ClientThread(socket, this);
                thread.start();
            }
        } catch (IOException e) {
            //
        }
    }

    // edit this + error handle message | loopback (l)
    public synchronized boolean send(Message message) {
        if (!message.isCorrect())
            return false;

        ClientThread c;
        switch (message.getType()) {
            case 'l':
                message.getSender().send(formatMessage(message));
                return true;
            case 'u':
                c = message.getSender();
                String name = message.getMessage();

                if (c.getUsername() != null && !c.getUsername().equals("")) {
                    send(new Message('l', c, "Username already set"));
                    return true;
                }

                if (clients.get(name) != null) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
                    name += "-" + sdf.format(cal.getTime());
                }

                if (c.setUsername(name)) {
                    clients.put(c.getUsername(), c);
                    send(new Message('l', c, "Your username is: " + name));
                    send(new Message('s', c, name +  " has connected to the server"));
                }
                return true;
            case 's':
            case 'm':
                broadcast(message);
                return true;
            case 'd':
                c = clients.get(message.getHeader());
                if (c != null) {
                    c.send(formatMessage(message));
                    return true;
                }

                send(new Message('l', message.getSender(), "User not found"));
                return true;
        }
        return false;
    }

    private String formatMessage(Message message) {
        String type = "";
        switch (message.getType()) {
            case 'u':
            case 's':
            case 'l':
                type = "System";
                break;
            case 'm':
            case 'd':
                type = message.getSender().getUsername() + " " + message.getType();
                break;
        }

        return String.format("[%s] %s", type, message.getMessage());
    }

    private synchronized void broadcast(Message message) {
        for (ClientThread c: clients.values()) {
            if (!c.equals(message.getSender())) {
                c.send(formatMessage(message));
            }
        }
    }

    public synchronized void disconnect(ClientThread thread, boolean lost) {
        if (thread.isJoin())
            clients.remove(thread.getUsername());

        if (lost) {
            cm.lost();
            send(new Message('s', null, thread.getUsername() + " lost connection"));
        } else {
            cm.disconnect();
            send(new Message('s', null, thread.getUsername() + " has disconnected"));
        }
    }
}
