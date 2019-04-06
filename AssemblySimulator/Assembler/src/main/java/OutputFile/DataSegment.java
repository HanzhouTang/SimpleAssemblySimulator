package OutputFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSegment {
    List<Byte> data = new ArrayList<>();
    Map<String,Integer> nameTable = new HashMap<>();
    void addNmae(String name){
        int currentLocation = data.size();
        nameTable.put(name,currentLocation);
    }

}
