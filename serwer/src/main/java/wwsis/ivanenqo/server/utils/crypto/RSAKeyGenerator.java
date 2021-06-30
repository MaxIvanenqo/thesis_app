package wwsis.ivanenqo.server.utils.crypto;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSAKeyGenerator {
    private static final String PUBLIC_KEY_FILE = "publicServer.key";
    private static final String PRIVATE_KEY_FILE = "privateServer.key";

    public static void main(String[] args) {
        RSAKeyGenerator.Generator();
    }

    public static void Generator(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            RSAKeyGenerator generateKeys = new RSAKeyGenerator();

            generateKeys.saveKeys(PUBLIC_KEY_FILE, rsaPublicKeySpec.getModulus().toByteArray(), rsaPublicKeySpec.getPublicExponent().toByteArray());
            generateKeys.saveKeys(PRIVATE_KEY_FILE, rsaPrivateKeySpec.getModulus().toByteArray(), rsaPrivateKeySpec.getPrivateExponent().toByteArray());

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

