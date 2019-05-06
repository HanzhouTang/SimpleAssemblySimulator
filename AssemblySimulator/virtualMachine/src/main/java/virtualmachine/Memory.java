package virtualmachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Memory {

    private List<Byte> data = new ArrayList<>();

    void setData(List<Byte> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    byte get(int location) {
        return data.get(location);
    }

    public int size() {
        return data.size();
    }
}
