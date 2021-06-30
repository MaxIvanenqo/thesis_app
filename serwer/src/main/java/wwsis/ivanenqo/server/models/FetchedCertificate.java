package wwsis.ivanenqo.server.models;

public class FetchedCertificate {
    private final String id;
    private String access_code;
    private final String key;
    private String lastUse;
    private String blocked;
    private String device_info;

    public FetchedCertificate(String id_, String access_code_, String key_){
        this.id = id_;
        this.access_code = access_code_;
        this.key = key_;
    }

    public FetchedCertificate(String id_, String _device_info, String key_, String blocked, String _lastUse){
        this.id = id_;
        this.device_info = _device_info;
        this.key = key_;
        this.blocked = blocked;
        this.lastUse = _lastUse;
    }

    public String getId() {
        return id;
    }

    public String getAccess_code() {
        return access_code;
    }

    public String getKey() {
        return key;
    }

    public String getLastUse() {
        return lastUse;
    }

    public String getBlocked() {
        return blocked;
    }

    public String getDeviceInfo() {
        return this.device_info;
    }
}
