package server;

/**
 * Message.
 */
public class Message {

    /**
     * Sender of the message.
     */
    private ClientThread sender;

    /**
     * Type of the message.
     * Support types are:
     * - l => loopback
     * - s => system
     * - m => message (will be broadcast)
     * - d => direct
     * - u => username
     */
    private char type;

    /**
     * Header of the message.
     */
    private String header;

    /**
     * Text of the message.
     */
    private String message;

    /**
     * Message is correct formatted.
     */
    private boolean correct;

    /**
     * Create a new Message out raw data.
     * @param sender Sender of the message.
     * @param message Raw message.
     */
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

            // parse the message of the raw message
            m = new String[parts.length - 2];
            System.arraycopy(parts, 2, m, 0, m.length);
        } else {
            if (parts.length < 2) {
                return;
            }

            // parse the message of the raw message
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

    /**
     * Create a message.
     * @param type Type of the message.
     *             See for supported {@link #type}s.
     * @param sender Sender of the message.
     * @param message Text of the message.
     */
    public Message(char type, ClientThread sender, String message) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        correct = true;
    }

    /**
     * Has given type a header.
     * @param type Type of the message.
     * @return has given type a header.
     */
    private boolean hasHeader(char type) {
        return type == 'd';
    }

    /**
     * Get the sender of the message.
     * @return the sender of the message.
     */
    public ClientThread getSender() {
        return sender;
    }

    /**
     * Get the type of the message.
     * @return the type of the message.
     */
    public char getType() {
        return type;
    }

    /**
     * Get the header of the message.
     * @return the header of the message.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get the text of the message.
     * @return the text of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Is the message correct formatted.
     * @return the message correct formatted.
     */
    public boolean isCorrect() {
        return correct;
    }
}
