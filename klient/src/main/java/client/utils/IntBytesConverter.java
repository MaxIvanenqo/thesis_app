package client.utils;

import java.nio.ByteBuffer;

public class IntBytesConverter {
    public static byte[] convertToBytes(int a){
        return ByteBuffer.allocate(4).putInt(a).array();
    }
    public static int convertToInt(byte[] bytes){
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getInt();
    }
}
