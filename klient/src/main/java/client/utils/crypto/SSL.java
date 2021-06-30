package client.utils.crypto;

import client.utils.ArrayKit;
import client.utils.IntBytesConverter;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SSL {

    private static final int KEY_SIZE = 128;
    private SecretKeySpec skeySpec;

    public byte[] protectKey(){
        return RSA.encryptWithServerKey(this.skeySpec.getEncoded());
    }

    public SSL(){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(KEY_SIZE);
            SecretKey skey = kgen.generateKey();
            byte[] secretKey = skey.getEncoded();
            this.skeySpec = new SecretKeySpec(secretKey, "AES");
            this.createVectorBytes();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private byte[] createVectorBytes(){
        byte[] bytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public byte[] encrypt(byte[] bytes){
        try {
            byte[] vector = this.createVectorBytes();
            IvParameterSpec parameterSpec = new IvParameterSpec(vector);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);
            byte[] enc = cipher.doFinal(bytes);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encVector = cipher.doFinal(vector);
            return ArrayKit.joinArrays(IntBytesConverter.convertToBytes(enc.length),
                   enc,
                   IntBytesConverter.convertToBytes(encVector.length),
                   encVector);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] bytes){
        try {
            ArrayKit.pointToStart();
            byte[] enc = ArrayKit.pullNextPartFromArray(bytes);
            byte[] vectorEnc = ArrayKit.pullNextPartFromArray(bytes);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] vector = cipher.doFinal(vectorEnc);
            IvParameterSpec parameterSpec = new IvParameterSpec(vector);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, parameterSpec);
            return cipher.doFinal(enc);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
}