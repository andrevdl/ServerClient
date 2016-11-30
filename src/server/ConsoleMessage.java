package server;

import java.io.PrintWriter;

public class ConsoleMessage {

    private PrintWriter writer;

    public ConsoleMessage() {
        writer = new PrintWriter(System.out);
    }

    private synchronized void message(String type, String message) {
        writer.printf("[%s]\t%s\n", type, message).flush();
    }

    public synchronized void connect() {
        message("connection", "Connected to a new Client");
    } // edit

    public synchronized void lost() {
        message("connection", "Lost connection");
    }

    public synchronized void disconnect() {
        message("connection", "Disconnect");
    }
}
