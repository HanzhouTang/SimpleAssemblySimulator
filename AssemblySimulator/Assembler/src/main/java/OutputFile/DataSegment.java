package OutputFile;

import javax.xml.crypto.Data;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The data segment
 * It contains a List&ltByte&rt to represent data.
 */
public class DataSegment implements SupportTwoParsingPass {
    List<Byte> data = new ArrayList<>();
    Map<String, Integer> nameTable = new HashMap<>();

    public void addNmae(String name) {
        int currentLocation = data.size();
        nameTable.put(name, currentLocation);
    }

    public List<Byte> getData() {
        return data;
    }

    public void addData(String str, DataType dataType) throws Exception {
        for (byte b : str.getBytes("US-ASCII")) {
            for (int i = 0; i < dataType.getSize() - 1; i++) {
                final byte tmp = 0;
                data.add(tmp);
            }
            data.add(b);
        }
    }

    public void addData(BigInteger number, DataType dataType) throws Exception {
        byte[] bytes = number.toByteArray();
        if (bytes.length <= dataType.getSize()) {
            for (int i = 0; i < dataType.getSize() - bytes.length; i++) {
                final byte tmp = 0;
                data.add(tmp);
            }
            for (byte b : bytes) {
                data.add(b);
            }
        } else {
            for (int i = bytes.length - dataType.getSize(); i < bytes.length; i++) {
                data.add(bytes[i]);
            }
        }
    }

    public void addData(List<Byte> bytes) throws Exception {
        data.addAll(bytes);
    }

    public List<Byte> getPortionFrom(int i) {

        List<Byte> tmp = new ArrayList<>();
        tmp.addAll(data.subList(i, data.size()));
        return tmp;
    }

    public int getCurrentLocation() {
        return data.size();
    }

    public int getLocationByName(String name) {
        return nameTable.getOrDefault(name, -1);
    }

    public Byte get(int index) {
        if (index < data.size()) {
            return data.get(index);
        } else {
            return null;
        }
    }

    @Override
    public void resetAfterFirstParsingPass(Object... params) throws Exception {
        data.clear();
    }
}
