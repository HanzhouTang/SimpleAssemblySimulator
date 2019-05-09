package virtualmachine;

public class Message {
    final String message;

    public Message(String msg) {
        message = msg;
    }

    @Override
    public String toString() {
        return message;
    }
}
