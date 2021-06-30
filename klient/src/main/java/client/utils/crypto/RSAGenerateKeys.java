package client.utils.crypto;

import client.Global;
import client.utils.PrivateFolderGen;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAGenerateKeys {
//    public static void main(String[] args) {
//        RSAGenerateKeys.Generator();
//    }

    public static void Generator(byte[] pass){
        try {
            final String PUBLIC_KEY_FILE = PrivateFolderGen.key(Global.getUsername(), "public");
            final String PRIVATE_KEY_FILE = PrivateFolderGen.key(Global.getUsername(), "private");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            RSAGenerateKeys generateKeys = new RSAGenerateKeys();

            AES aes = new AES();

            byte[] privateExpEncoded = rsaPrivateKeySpec.getPrivateExponent().toByteArray();
            byte[] privateExpEncrypted = aes.encryptPrivateKey(privateExpEncoded, pass);
            BigInteger bi = new BigInteger(privateExpEncrypted);

            generateKeys.saveKeys(PUBLIC_KEY_FILE, rsaPublicKeySpec.getModulus().toByteArray(), rsaPublicKeySpec.getPublicExponent().toByteArray());
            generateKeys.saveKeys(PRIVATE_KEY_FILE, rsaPrivateKeySpec.getModulus().toByteArray(), bi.toByteArray());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println(e);
        }
    }

    private void saveKeys (String fileName, byte[] mod, byte[] exp){
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            oos.writeInt(mod.length);
            oos.write(mod);
            oos.writeInt(exp.length);
            oos.write(exp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
