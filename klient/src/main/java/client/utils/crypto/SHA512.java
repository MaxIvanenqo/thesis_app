package client.utils.crypto;

import client.utils.ArrayKit;
import client.utils.IntBytesConverter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SHA512 {
    public byte[] hash(String data_){
        byte[] salt = this.createSalt();
        byte[] data = data_.getBytes(StandardCharsets.UTF_8);
        byte[] generatedHash = this.generateHash(data, salt);
        assert generatedHash != null;
        return ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(generatedHash.length),
                generatedHash,
                IntBytesConverter.convertToBytes(salt.length),
                salt);
    }

    public byte[] hashGen(byte[] data, byte[] salt){
        return this.generateHash(data, salt);
    }

    public byte[] hash(byte[] data, byte[] salt){
        byte[] generatedHash = this.generateHash(data, salt);
        assert generatedHash != null;
        return ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(generatedHash.length),
                generatedHash,
                IntBytesConverter.convertToBytes(salt.length),
                salt);
    }

    public byte[] restoreHash(byte[] data, byte[] salt){
        return this.generateHash(data, salt);
    }

    private byte[] generateHash(byte[] data, byte[] salt){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(salt);
            return messageDigest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] createSalt(){
        SecureRandom r = new SecureRandom();
        byte saltSize;
        do {
            byte saltBytesSizeMod = 127;
            saltSize = (byte) (Math.abs(r.nextInt()) % saltBytesSizeMod);
        } while (saltSize < 64);
        byte[] bytes = new byte[saltSize];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
