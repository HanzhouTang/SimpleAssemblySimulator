package virtualmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Component
public class Memory {

    private byte[] data = null;

    void setData(List<Byte> data) {
        this.data = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            this.data[i] = data.get(i);
        }

    }

    byte get(int location) {
        return data[location];
    }

    // read 32 bits.
    public int get32(int location) {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[i] = data[i + location];
        }
        ByteBuffer bb = ByteBuffer.wrap(tmp);
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getInt();
    }

    byte[] getData() {
        return data;
    }

    public void set(int location, int value) {
        // need concern
    }

    public int size() {
        return data.length;
    }
}
