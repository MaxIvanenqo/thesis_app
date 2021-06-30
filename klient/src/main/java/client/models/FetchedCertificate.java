package client.models;

public class FetchedCertificate {
    private final String id;
    private final String key;
    private String blocked;
    private String last_use;
    private String device_name;

    public FetchedCertificate(String id_, String key_){
        this.id = id_;
        this.key = key_;
    }

    public FetchedCertificate(String id_, String _device_name, String key_, String _blocked, String _last_use){
        this.id = id_;
        this.device_name = _device_name;
        this.key = key_;
        this.blocked = _blocked;
        this.last_use = _last_use;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public boolean getBlocked() {
        return blocked.equals("0");
    }

    public String getLast_use() {
        return last_use;
    }
}
