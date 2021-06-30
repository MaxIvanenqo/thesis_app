package client.models;

import controllers.included.message.Attachment;

public class DecryptedMessage {
    private final String msg;
    private final String time;
    private final boolean own;
    private final String dialogTargets;
    private final Attachment attachment;
    private final String attName;
    private final String id;

    public DecryptedMessage(String id, byte[] attName, String msg, String time, boolean b, String email, Attachment attachment){
        this.msg = msg;
        this.time = time;
        this.own = b;
        this.dialogTargets = email;
        this.attachment = attachment;
        this.attName = attName!=null? new String(attName):null;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAttName() {
        return attName;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public String getDialogTargets() {
        return dialogTargets;
    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }

    public boolean getOwner(){
        return this.own;
    }
}
