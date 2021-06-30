package controllers.included.message;

import client.models.FetchedCertificate;
import client.utils.ArrayKit;
import client.utils.ByteHexStringConverter;
import client.utils.IntBytesConverter;
import client.utils.crypto.AES;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static client.utils.crypto.RSA.restorePublicKey;

public class Attachment {
    private byte[] data;
    private File file;

    public Attachment(){
        this.data = new byte[0];
    }

    public Attachment(File file) {
        this.file = file;
        try {
            this.data = Files.readAllBytes(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getFileNameBytes(){
        if (file==null) return new byte[0];
        if (data.length == 0) return new byte[0];
        return ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(this.file.getName().getBytes().length),
                this.file.getName().getBytes()
        );
    }

    public void restoreAndSave(String path){
        if (this.data==null) return;
        try {
            Files.write(Paths.get(path), this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] convertToBytesAndEncrypt(FetchedCertificate certificate){
        if (file==null) return new byte[0];
        try {
            byte[] bytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
            AES aes = new AES();
            byte[] encBytes = aes.encrypt(bytes);
            String encKey = ByteHexStringConverter.bytesToStringHex(aes.protectKey(restorePublicKey(certificate.getKey())));
            byte[] eSign = aes.makeESign();
            return ArrayKit.joinArrays(
                    IntBytesConverter.convertToBytes(encBytes.length),
                    encBytes,
                    IntBytesConverter.convertToBytes(encKey.getBytes().length),
                    encKey.getBytes(),
                    IntBytesConverter.convertToBytes(eSign.length),
                    eSign
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void main(String[] args) throws IOException {
        File f = new File("/home/maxivanenqo/Documents/projects/thesisClient/sample.pdf");
        File o = new File("/home/maxivanenqo/Documents/projects/thesisClient/out.pdf");
        Attachment pdf = new Attachment(f);
        pdf.restoreAndSave(o.getPath());
    }
}