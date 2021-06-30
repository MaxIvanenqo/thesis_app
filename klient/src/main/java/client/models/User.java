package client.models;

import client.utils.ArrayKit;

public class User {
    private String identificator;

    private String id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String description;

    private String userPhoto;

    private boolean iam;

    private boolean isOnline;

    private boolean gotNewMsg;

    private String lastSeen;

    private boolean isContact;

    public String getLastSeen() {
        return lastSeen;
    }

    public boolean isContact() {
        return isContact;
    }

    public boolean isGotNewMsg() {
        return gotNewMsg;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public User() {}

    public User (boolean isOnline, String id, String identificator, String fullName, String userPhoto, String email, String description, String phoneNumber, String gotNewMsg, String lastSeen, boolean isContact){
        this.isOnline = isOnline;
        this.id = id;
        this.identificator = identificator;
        this.fullName = fullName;
        this.userPhoto = userPhoto;
        this.email = email;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.lastSeen = lastSeen;
        this.gotNewMsg = gotNewMsg.equals("1");
        this.isContact = isContact;
    }

    public User (byte[] user){
        ArrayKit.pointToStart();
        this.isOnline = new String(ArrayKit.pullNextPartFromArray(user)).equals("1");
        this.id = new String(ArrayKit.pullNextPartFromArray(user));
        this.identificator = new String(ArrayKit.pullNextPartFromArray(user));
        this.fullName = new String(ArrayKit.pullNextPartFromArray(user));
        this.userPhoto = new String(ArrayKit.pullNextPartFromArray(user));
        this.email = new String(ArrayKit.pullNextPartFromArray(user));
        this.description = new String(ArrayKit.pullNextPartFromArray(user));
        this.phoneNumber = new String(ArrayKit.pullNextPartFromArray(user));
        this.gotNewMsg = new String(ArrayKit.pullNextPartFromArray(user)).equals("1");
        this.lastSeen = new String(ArrayKit.pullNextPartFromArray(user));
        this.isContact = new String(ArrayKit.pullNextPartFromArray(user)).equals("1");
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdentificator(String id) {
        this.identificator = id;
    }

    public void setIam(boolean iam) {
        this.iam = iam;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserPhoto(String photo) {
        this.userPhoto = photo;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getIdentificator() {
        return identificator;
    }

    public boolean isIam(){
        return iam;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

}
