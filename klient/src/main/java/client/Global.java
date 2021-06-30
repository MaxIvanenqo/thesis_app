package client;

public class Global {
    public static Client client;
    private static String username;
    public static byte[] password;

    public static final int DISCONNECTION  = 2;
    public static final int SEND_MESSAGE = 4;


    public static final int CONVERSATION_INIT = 30;

    public static final int LOAD_USERS_REQ = 60;
    public static final int LOAD_USERS_RES = 61;

    public static final int LOG_OUT        = 65;

    public static final int LIST_OF_POSSIBLE_CHOICES_REQ = 70;
    public static final int LIST_OF_POSSIBLE_CHOICES_RES = 71;

    public static final int SIGNING_UP = 80;
    public static final int SIGN_CONFIRM = 84;
    public static final int SIGN_SUCCESS = 85;

    public static final int LOG_ENTER = 95;
    public static final int LOG_STATUS = 96;
    public static final int LOG_ERROR = 97;
    public static final int LOGGED         = 98;
    public static final int LOGGING        = 99;

    public static final int RESET_MESSAGES_READ_STATE = 105;

    public static final int EDIT_PERSONAL_DATA = 110;
    public static final int EDIT_PERSONAL_DATA_SUCCEEDED = 111;

    public static final int FIND_PROFILE = 200;
    public static final int FIND_PROFILE_TIP = 201;
    public static final int FIND_PROFILE_BY_ID = 202;

    public static final int ADD_CONTACT = 300;
    public static final int REMOVE_FROM_CONTACT_LIST = 303;
    public static final int LOAD_ATTACHMENT_REQ             = 505;
    public static final int LOAD_ATTACHMENT_RES             = 506;

    public static final int LOAD_DEVICES_LIST_REQ = 600;
    public static final int LOAD_DEVICES_LIST_RES           = 601;

    public static final int CHANGE_CERTIFICATE_STATUS_REQ = 700;

    public static final int FORCED_LOGOUT                    = 800;

    public static final int BURN_MESSAGES = 900;

    public static final int BURN_MESSAGES_DONE = 901;

    public static final int MAKE_SSL = 1001;

    public static void setUsername(String username) {
        Global.username = username;
    }

    public static String getUsername() {
        return username;
    }
}
