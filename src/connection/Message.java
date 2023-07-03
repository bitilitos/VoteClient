package connection;

import java.io.Serializable;
import java.net.InetAddress;

public class Message implements Serializable {

    private final InetAddress SENDER;
    private final InetAddress RECEIVER;
    private final MessageType MSG_TYPE;
    private final String CONTENT;

    public Message(InetAddress sender, InetAddress receiver, MessageType msgType, String content) {
        this.CONTENT = content;
        this.SENDER = sender;
        this.RECEIVER = receiver;
        this.MSG_TYPE = msgType;
    }

    public InetAddress getSENDER() {
        return SENDER;
    }

    public MessageType getMSG_TYPE() {
        return MSG_TYPE;
    }

    public String getCONTENT() {
        return CONTENT;
    }

    public InetAddress getRECEIVER() {
        return RECEIVER;
    }
}
