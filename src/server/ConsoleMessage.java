package server;

import java.io.PrintWriter;

/**
 * Helper class to format send messages.
 */
public class ConsoleMessage {

    /**
     * Writer.
     */
    private PrintWriter writer;

    /**
     * Create instance of the helper class.
     */
    public ConsoleMessage() {
        writer = new PrintWriter(System.out);
    }

    /**
     * Format standard message.
     * @param type Type of the message.
     * @param message Text of the message.
     */
    private synchronized void message(String type, String message) {
        writer.printf("[%s]\t%s\n", type, message).flush();
    }

    /**
     * Format a connection message.
     */
    public synchronized void connect() {
        message("connection", "Connected to a new Client");
    }

    /**
     * Format a connection lost message.
     */
    public synchronized void lost() {
        message("connection", "Lost connection");
    }

    /**
     * Format a disconnection message.
     */
    public synchronized void disconnect() {
        message("connection", "Disconnect");
    }
}
