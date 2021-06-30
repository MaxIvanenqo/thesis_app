package wwsis.ivanenqo.server.models;

import wwsis.ivanenqo.server.utils.ArrayKit;
import wwsis.ivanenqo.server.utils.HexArray;
import wwsis.ivanenqo.server.utils.IntByteArray;

public class EncryptedMessage {
    private String id;
    private String certificateTargetId;
    private final String targetUserId;
    private final String msg;
    private String senderPubKey;
    private final String protectedKey;
    private String timestamp;
    private String senderUserId;
    private String attachment;

    public EncryptedMessage(byte[] msg){
        ArrayKit.pointToStart();
        this.certificateTargetId = new String(ArrayKit.pullNextPartFromArray(msg));
        this.targetUserId = new String(ArrayKit.pullNextPartFromArray(msg));
        this.protectedKey = new String(ArrayKit.pullNextPartFromArray(msg));
        this.attachment = HexArray.bytesToStringHex(ArrayKit.pullNextPartFromArray(msg));
        this.msg = HexArray.bytesToStringHex(ArrayKit.pullNextPartFromArray(msg));
    }

    public String getAttachment() {
        if (attachment==null) return "";
        return attachment;
    }

    public EncryptedMessage(String id,
                            String senderUserId,
                            String addressUserId,
                            String senderPubKey,
                            String timestamp,
                            String msg,
                            String protectedKey) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.targetUserId = addressUserId;
        this.senderPubKey = senderPubKey;
        this.timestamp = timestamp;
        this.msg = msg;
        this.protectedKey = protectedKey;
    }

    public String getProtectedKey() {
        return protectedKey;
    }

    public byte[] toBytes(){
        return ArrayKit.joinArrays(
                IntByteArray.convertToBytes(this.id.getBytes().length),
                this.id.getBytes(),
                IntByteArray.convertToBytes(this.protectedKey.getBytes().length),
                this.protectedKey.getBytes(),
                IntByteArray.convertToBytes(this.senderPubKey.getBytes().length),
                this.senderPubKey.getBytes(),
                IntByteArray.convertToBytes(this.timestamp.getBytes().length),
                this.timestamp.getBytes(),
                IntByteArray.convertToBytes(this.senderUserId.getBytes().length),
                this.senderUserId.getBytes(),
                IntByteArray.convertToBytes(this.msg.getBytes().length),
                this.msg.getBytes()
        );
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public String getCertTargetId() {
        return certificateTargetId;
    }

    public String getMsg() {
        return msg;
    }
}
