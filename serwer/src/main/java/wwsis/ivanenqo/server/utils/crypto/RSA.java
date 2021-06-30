package wwsis.ivanenqo.server.utils.crypto;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;

public class RSA {

    public static byte[] encrypt(byte[] data, byte[] key){
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
            Cipher cipherEncrypt = Cipher.getInstance("RSA");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipherEncrypt.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] data){
        try {
            PrivateKey privateKey = readPrivateKeyFromFile();
            Cipher cipherEncrypt = Cipher.getInstance("RSA");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipherEncrypt.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data){
        try {
            PrivateKey privateKey = RSA.readPrivateKeyFromFile();
            Cipher cipherDecrypt = Cipher.getInstance("RSA");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
            return cipherDecrypt.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey readPrivateKeyFromFile(){
        try {
            FileInputStream fis = new FileInputStream("privateServer.key");
            ObjectInputStream ois = new ObjectInputStream(fis);
            int modLength = ois.readInt();
            byte[] modBytes = ois.readNBytes(modLength);
            int expLength = ois.readInt();
            byte[] expBytes = ois.readNBytes(expLength);
            BigInteger modulus  = new BigInteger(modBytes);
            BigInteger exponent = new BigInteger(expBytes);
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            ois.close();
            fis.close();
            return factory.generatePrivate(rsaPrivateKeySpec);
        }
        catch (Exception e){
            return null;
        }
    }
}
