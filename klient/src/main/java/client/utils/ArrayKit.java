package client.utils;

public class ArrayKit {
    private static int pos = 0;
    public static void pointToStart() {
        ArrayKit.pos = 0;
    }

    public static byte[] joinArrays(byte[] ... args){
        int size = 0;
        for (byte[] bytes: args){
            size += bytes.length;
        }
        byte[] main = new byte[size];
        int pos = 0;
        for(byte[] bytes : args){
            System.arraycopy(bytes, 0, main, pos, bytes.length);
            pos += bytes.length;
        }
        return main;
    }

    public static byte[] joinArrays(byte[] x1, byte[] x2){
        byte[] r = new byte[x1.length+ x2.length];
        System.arraycopy(x1, 0, r, 0, x1.length);
        System.arraycopy(x2, 0, r, x1.length, x2.length);
        return r;
    }

    public static byte[] joinArraysWithConst(int CONST, byte[] ... args){
        byte[] c = IntBytesConverter.convertToBytes(CONST);
        int size = 4;
        for (byte[] bytes: args){
            size += bytes.length + 4;
        }
        byte[] main = new byte[size];
        System.arraycopy(c, 0, main, 0, 4);
        int pos = 4;
        for(byte[] bytes : args){
            System.arraycopy(IntBytesConverter.convertToBytes(bytes.length), 0, main, pos, 4);
            pos += 4;
            System.arraycopy(bytes, 0, main, pos, bytes.length);
            pos += bytes.length;
        }
        return main;
    }

    public static byte[] pullNextPartFromArray(byte[] full){
        byte[] length_ = new byte[4];
        System.arraycopy(full, ArrayKit.pos, length_, 0, 4);
        ArrayKit.pos += 4;
        int length = IntBytesConverter.convertToInt(length_);
        byte[] value = new byte[length];
        System.arraycopy(full, ArrayKit.pos, value, 0, length);
        ArrayKit.pos += value.length;
        return value;
    }
}
