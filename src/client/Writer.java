package client;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Writer extends Thread {

    private PrintWriter writer;

    private PrintWriter console;

    private Scanner scanner;

    public Writer(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        writer = new PrintWriter(outputStream);

        scanner = new Scanner(System.in);
        console = new PrintWriter(System.out);
    }

    @Override
    public void run() {
        console.println("Welcome to the chat lobby!\n");
        console.flush();

        username();

        help();

        while (true) {
            send();
        }
    }

    private void username() {
        String username;
        do {
            console.println("Please enter your username? \nAllowed characters are: a-zA-Z0-9_");
            console.flush();

            username = scanner.nextLine();
        } while (!Pattern.matches("[a-zA-Z0-9_]+", username));

        writer.println("u " + username);
        writer.flush();
    }

    private void help() {
        console.println("\nTo send a message use the follow syntax: <type> (<username>) <message>.");
        console.println("Supported types are: d(direct), m(message).");
        console.println("For help press the '?' key on your keyboard.\n");
        console.flush();
    }

    private void send() {
        String m = scanner.nextLine();
        if (m.length() < 1) {
            console.println("Message has incorrect format, press '?' for help!");
            console.flush();
            return;
        }

        String[] parts = m.split(" ");

        switch (m.charAt(0)) {
            case '?':
                help();
                break;
            case 'd':
                if (parts.length < 3) {
                    console.println("Message has incorrect format, press '?' for help!");
                    console.flush();
                    break;
                }
            case 'm':
                if (parts.length < 2 || m.charAt(1) != ' ') {
                    console.println("Message has incorrect format, press '?' for help!");
                    console.flush();
                    break;
                }

                writer.println(m);
                writer.flush();
                break;
            default:
                console.println("Message type is not supported, press '?' for help!");
                console.flush();
        }
    }
}
