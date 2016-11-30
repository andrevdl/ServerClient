package server;

public class Message {

    private ClientThread sender;

    private char type;

    private String header;

    private String message;

    private boolean correct;

    public Message(ClientThread sender, String message) {
        if (message.length() < 3 || message.charAt(1) != ' ') {
            return;
        }

        this.sender = sender;
        type = message.charAt(0);
        String[] parts = message.split(" ");
        String[] m;

        if (hasHeader(type)) {
            if (parts.length < 3) {
                return;
            }

            header = parts[1];

            m = new String[parts.length - 2];
            System.arraycopy(parts, 2, m, 0, m.length);
        } else {
            if (parts.length < 2) {
                return;
            }

            m = new String[parts.length - 1];
            System.arraycopy(parts, 1, m, 0, m.length);
        }

        StringBuilder builder = new StringBuilder();
        for(String s : m) {
            builder.append(s);
        }

        this.message = builder.toString();
        correct = true;
    }

    public Message(char type, ClientThread sender, String message) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        correct = true;
    }

    private boolean hasHeader(char type) {
        return type == 'd';
    }

    public ClientThread getSender() {
        return sender;
    }

    public char getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCorrect() {
        return correct;
    }
}
