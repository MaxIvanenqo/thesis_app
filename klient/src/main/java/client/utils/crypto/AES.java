package client.utils.crypto;

import client.Global;
import client.utils.ArrayKit;
import client.utils.IntBytesConverter;
import client.utils.PrivateFolderGen;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;

import static client.utils.crypto.RSA.restorePublicKey;

public class AES {
    private SecretKey   secretKey;
    private byte[] vectorBytes;

    public AES(){
        try {
            this.keyGenerator(256);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] protectKey(PublicKey publicKey) {
        return RSA.encrypt(this.secretKey.getEncoded(), publicKey);
    }

    private byte[] createVectorBytes(){
        byte[] bytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public byte[] makeESign() {
        SHA512 sha512 = new SHA512();
        byte[] salt = sha512.createSalt();
        byte[] keyHash = sha512.hashGen(this.secretKey.getEncoded(), salt);
        PrivateKey privateKey = RSA.readPrivateKeyFromFile(
                PrivateFolderGen.key(Global.getUsername(), "private"));
        byte[] protectedHash = RSA.encrypt(keyHash, privateKey);
        assert protectedHash != null;
        return ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(salt.length),
                salt,
                IntBytesConverter.convertToBytes(protectedHash.length),
                protectedHash
        );
    }

    public void keyGenerator(final int keySize) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(keySize);
        SecretKey skey = kgen.generateKey();
        byte[] secretKey = skey.getEncoded();
        this.vectorBytes = this.createVectorBytes();
        this.secretKey = new SecretKeySpec(secretKey, "AES");
    }

    public byte[] decrypt(final byte[] data, byte[] encryptedAESKey){
        try {
            ArrayKit.pointToStart();
            byte[] dataMsg = ArrayKit.pullNextPartFromArray(data);
            byte[] dataVector = ArrayKit.pullNextPartFromArray(data);
            byte[] decryptedAESKey = RSA.decrypt(encryptedAESKey);
            assert decryptedAESKey != null;
            SecretKeySpec key = new SecretKeySpec(decryptedAESKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] vectorDec = cipher.doFinal(dataVector);
            IvParameterSpec parameterSpec = new IvParameterSpec(vectorDec);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            return cipher.doFinal(dataMsg);
        }
        catch (BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encrypt(final byte[] data) {
        try {
            byte[] vector = this.createVectorBytes();
            IvParameterSpec parameterSpec = new IvParameterSpec(vector);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] enc = cipher.doFinal(data);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
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

    public byte[] encryptPrivateKey(final byte[] data, final byte[] key_encoded) {
        try {
            SecretKeySpec key = new SecretKeySpec(key_encoded, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        }
        catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptPrivateKey(final byte[] data, final byte[] key_encoded) {
        try {
            SecretKeySpec key = new SecretKeySpec(key_encoded, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        }
        catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            return null;
        }
    }

    public boolean eSignCheck(byte[] eSign, String pubKeySender, byte[] encryptedKey){
        ArrayKit.pointToStart();
        byte[] salt = ArrayKit.pullNextPartFromArray(eSign);
        byte[] hashProtected = ArrayKit.pullNextPartFromArray(eSign);
        PublicKey publicKey = restorePublicKey(pubKeySender);
        SHA512 sha512 = new SHA512();
        byte[] decryptedKey = RSA.decrypt(encryptedKey);
        assert decryptedKey != null;
        SecretKey key = new SecretKeySpec(decryptedKey, "AES");
        byte[] hashRepeated = sha512.restoreHash(key.getEncoded(), salt);
        byte[] hashFetched = RSA.decrypt(hashProtected, publicKey);
        return Arrays.equals(hashFetched, hashRepeated);
    }
}
