package wwsis.ivanenqo.server.utils;

import java.nio.ByteBuffer;

public class IntByteArray {
    public static byte[] convertToBytes(int n){
        return ByteBuffer.allocate(4).putInt(n).array();
    }
    public static int convertToInt(byte[] bytes){
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getInt();
    }
}