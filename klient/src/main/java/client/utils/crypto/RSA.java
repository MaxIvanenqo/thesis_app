package client.utils.crypto;

import client.Global;
import client.utils.ByteHexStringConverter;
import client.utils.PrivateFolderGen;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class RSA {
    public static byte[] encrypt(byte[] data, PrivateKey privateKey){
        try {
            Cipher cipherEncrypt = Cipher.getInstance("RSA");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipherEncrypt.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey){
        try {
            Cipher cipherEncrypt = Cipher.getInstance("RSA");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipherEncrypt.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data){
        try {
            PrivateKey pk = readPrivateKeyFromFile(PrivateFolderGen.key(Global.getUsername(), "private"));
            Cipher cipherDecrypt = Cipher.getInstance("RSA");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, pk);
            return cipherDecrypt.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data, PublicKey publicKey){
        try {
            Cipher cipherDecrypt = Cipher.getInstance("RSA");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, publicKey);
            return cipherDecrypt.doFinal(data);
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHexOfPublicKey(){
        return ByteHexStringConverter.bytesToStringHex(Objects.requireNonNull(readPublicKeyFromFile(PrivateFolderGen.key(Global.getUsername(), "public"))).getEncoded());
    }

    public static PublicKey restorePublicKey(String keyHex){
        byte[] keyBytes = ByteHexStringConverter.hexStringToBytes(keyHex);
        try {
            KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return rsaKeyFac.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptWithServerKey(byte[] data){
        PublicKey publicKey = RSA.readPublicKeyFromFile("publicServer.key");
        try {
            Cipher cipherDecrypt = Cipher.getInstance("RSA");
            cipherDecrypt.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipherDecrypt.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptWithServerKey(byte[] data){
        PublicKey publicKey = RSA.readPublicKeyFromFile("publicServer.key");
        try {
            Cipher cipherDecrypt = Cipher.getInstance("RSA");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, publicKey);
            return cipherDecrypt.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey readPrivateKeyFromFile(String fileName){
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            int modLength = ois.readInt();
            byte[] modBytes = ois.readNBytes(modLength);
            int expLength = ois.readInt();
            byte[] expBytes = ois.readNBytes(expLength);
            BigInteger modulus  = new BigInteger(modBytes);

            AES aes = new AES();
            byte[] decryptedExp = aes.decryptPrivateKey(expBytes, Global.password);
            BigInteger exponent = new BigInteger(decryptedExp);

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

    public static PublicKey readPublicKeyFromFile(String fileName){
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            int modLength = ois.readInt();
            byte[] modBytes = ois.readNBytes(modLength);
            int expLength = ois.readInt();
            byte[] expBytes = ois.readNBytes(expLength);
            BigInteger modulus  = new BigInteger(modBytes);
            BigInteger exponent = new BigInteger(expBytes);
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            ois.close();
            fis.close();
            return factory.generatePublic(rsaPublicKeySpec);
        }
        catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            e.printStackTrace();
        }
        return null;
    }
}
