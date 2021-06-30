package client.models;

import client.App;
import client.Client;
import client.utils.ArrayKit;
import client.utils.ByteHexStringConverter;
import client.utils.IntBytesConverter;
import client.utils.crypto.AES;
import controllers.included.message.Attachment;
import javafx.application.Platform;

import static client.utils.crypto.RSA.restorePublicKey;


public class EncMsg {
    private byte[] msgToSend;
    private String msg;
    private String protectedKey;
    private byte[] eSign;
    private FetchedCertificate certificate;
    private Attachment attachment;
    private final String attachmentExist;
    private final String dialogTargets;

    public EncMsg(byte[] msg,
                  FetchedCertificate certificate_,
                  String target,
                  Attachment attachment){
        this.msgToSend = msg; // Plain
        this.attachment = attachment==null?new Attachment():attachment;
        this.attachmentExist = attachment==null?"0":"1";
        this.certificate = certificate_;
        this.dialogTargets = target;
    }

    public byte[] toBytes(){
        final AES aes = new AES();
        this.protectedKey = ByteHexStringConverter.bytesToStringHex(aes.protectKey(restorePublicKey(certificate.getKey())));
        this.eSign = aes.makeESign();
        byte[] pdf = this.attachment.convertToBytesAndEncrypt(this.certificate);
        byte[] message;
        message = ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(this.dialogTargets.getBytes().length),
                this.dialogTargets.getBytes(),
                IntBytesConverter.convertToBytes(this.eSign.length),
                this.eSign,
                IntBytesConverter.convertToBytes(this.msgToSend.length),
                this.msgToSend,
                IntBytesConverter.convertToBytes(this.attachmentExist.getBytes().length),
                this.attachmentExist.getBytes(),
                IntBytesConverter.convertToBytes(this.attachment.getFileNameBytes().length),
                this.attachment.getFileNameBytes()
        );
        byte[] wholeEncrypt = aes.encrypt(message); // Cipher
        return ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(this.certificate.getId().getBytes().length),
                this.certificate.getId().getBytes(),
                IntBytesConverter.convertToBytes(dialogTargets.split("_")[0].getBytes().length),
                dialogTargets.split("_")[0].getBytes(),
                IntBytesConverter.convertToBytes(this.protectedKey.getBytes().length),
                this.protectedKey.getBytes(),
                IntBytesConverter.convertToBytes(pdf.length),
                pdf,
                IntBytesConverter.convertToBytes(wholeEncrypt.length),
                wholeEncrypt);
    }

    public EncMsg(byte[] arr){
        byte[] x = new byte[arr.length-4];
        System.arraycopy(arr, 4, x, 0, x.length);
        ArrayKit.pointToStart();
        String id = new String(ArrayKit.pullNextPartFromArray(x));
        this.protectedKey = new String(ArrayKit.pullNextPartFromArray(x));
        String pubKeySender = new String(ArrayKit.pullNextPartFromArray(x));
        String timestamp = new String(ArrayKit.pullNextPartFromArray(x));
        boolean own = new String(ArrayKit.pullNextPartFromArray(x)).equals(Client.getInstance().getUser().getId());

        byte[] encMessage = ByteHexStringConverter.hexStringToBytes(new String(ArrayKit.pullNextPartFromArray(x)));
        AES aes = new AES();

        byte[] decMessage = aes.decrypt(encMessage, ByteHexStringConverter.hexStringToBytes(this.protectedKey));
        ArrayKit.pointToStart();
        this.dialogTargets = new String(ArrayKit.pullNextPartFromArray(decMessage));
        this.eSign = ArrayKit.pullNextPartFromArray(decMessage);
        this.msg = new String(ArrayKit.pullNextPartFromArray(decMessage));
        this.attachmentExist = new String(ArrayKit.pullNextPartFromArray(decMessage));
        byte[] attName = null;
        if (this.attachmentExist.equals("1")){
            byte[] attNameAndType = ArrayKit.pullNextPartFromArray(decMessage);
            ArrayKit.pointToStart();
            attName = ArrayKit.pullNextPartFromArray(attNameAndType);
        }
        boolean isSigned = aes.eSignCheck(this.eSign, pubKeySender, ByteHexStringConverter.hexStringToBytes(this.protectedKey));

        if (isSigned){
            DecryptedMessage dm = new DecryptedMessage(id, attName, this.msg, timestamp, own, this.dialogTargets, this.attachment);
            Platform.runLater(()-> Client.getInstance().getMainViewController().getMessengerController().messageCreator(dm));
        }
    }

}
