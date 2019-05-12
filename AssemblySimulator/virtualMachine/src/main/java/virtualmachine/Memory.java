package virtualmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    byte[] getData() {
        return data;
    }

    public void set(int location, int value){
    // need concern
    }

    public int size() {
        return data.length;
    }
}
