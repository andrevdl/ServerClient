package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Server.
 */
public class Server {

    /**
     * Port of the server.
     */
    private static final int SERVER_PORT = 4000;

    /**
     * Hash map with all joined clients.
     */
    private HashMap<String, ClientThread> clients;

    /**
     * Helper class to format messages.
     */
    private ConsoleMessage cm;

    /**
     * Create new server.
     * @param args Arguments
     */
    public static void main(String[] args) {
        new Server().run();
    }

    /**
     * Handle clients that connect to the server.
     */
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

    /**
     * Send a message.
     * @param message Message to send.
     * @return Message could be send.
     */
    public synchronized boolean send(Message message) {
        if (!message.isCorrect())
            return false;

        ClientThread c;
        switch (message.getType()) {

            // loopback message to the client
            case 'l':
                message.getSender().send(formatMessage(message));
                return true;

            // set username message, already set username's, can't be changed.
            case 'u':
                c = message.getSender();
                String name = message.getMessage();

                if (c.getUsername() != null && !c.getUsername().equals("")) {
                    send(new Message('l', c, "Username already set"));
                    return true;
                }

                // set time after name if somebody already used this username.
                if (clients.get(name) != null) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
                    name += "-" + sdf.format(cal.getTime());
                }

                // tell to others than somebody has entered the chat.
                if (c.setUsername(name)) {
                    clients.put(c.getUsername(), c);
                    send(new Message('l', c, "Your username is: " + name));
                    send(new Message('s', c, name +  " has connected to the server"));
                }
                return true;

            // send message to all clients in the chat
            case 's':
            case 'm':
                broadcast(message);
                return true;

            // send direct message to given user.
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

    /**
     * Format a message.
     * @param message Message to format.
     * @return formatted message.
     */
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

    /**
     * Broadcast a message to all joined clients.
     * @param message Message to broadcast.
     */
    private synchronized void broadcast(Message message) {
        for (ClientThread c: clients.values()) {
            if (!c.equals(message.getSender())) {
                c.send(formatMessage(message));
            }
        }
    }

    /**
     * Disconnect client from the server.
     * @param thread Client to disconnect.
     * @param lost Is connection lost, or is it only closed the connection.
     */
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
