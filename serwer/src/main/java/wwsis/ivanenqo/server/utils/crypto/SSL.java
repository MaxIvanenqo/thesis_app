package wwsis.ivanenqo.server.utils.crypto;

import wwsis.ivanenqo.server.utils.ArrayKit;
import wwsis.ivanenqo.server.utils.IntByteArray;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class SSL {

    private final SecretKeySpec skeySpec;

    public SSL(byte[] restoredKey){
        this.skeySpec = new SecretKeySpec(restoredKey, "AES");
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
            return ArrayKit.joinArrays(IntByteArray.convertToBytes(enc.length),
                    enc,
                    IntByteArray.convertToBytes(encVector.length),
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
