package client.models;

public class UserShort {
    private final String id;
    private final String identificator;
    private final String full_name;
    private String email;

    public String getId() {
        return id;
    }

    public UserShort(String id, String identificator, String full_name, String email) {
        this.id = id;
        this.identificator = identificator;
        this.full_name = full_name;
        this.email = email;
    }

    public String getIdentificator() {
        return identificator;
    }

    public String getFullName() {
        return full_name;
    }

    public String getEmail() {
        return this.email;
    }
}
