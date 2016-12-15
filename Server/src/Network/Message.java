package Network;

import java.io.Serializable;

public class Message implements Serializable {
    String header;
    Object payload;

    public Message(String header, Object payload) {
        this.header = header;
        this.payload = payload;
    }

    public String getHeader() {
        return header;
    }

    public Object getPayload() {
        return payload;
    }
}
