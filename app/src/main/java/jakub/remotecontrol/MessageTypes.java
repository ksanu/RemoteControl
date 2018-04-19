package jakub.remotecontrol;

/**
 * Created by Jakub on 19.04.2018.
 */

public interface MessageTypes {
    //sent by server:
    String PASSWORD_SALT = "PASSWORD_SALT";
    String AUTHORIZATION_RESULT = "AUTHORIZATION_RESULT";
    String AVAILABLE_ACTION = "AVAILABLE_ACTION";
    String SERVER_STATE = "SERVER_STATE";

    //sent by client:
    String PASSWORD_HASH = "PASSWORD_HASH";
    String CLIENT_STATE = "CLIENT_STATE";
    String EXECUTE_ACTION = "RUN_ACTION";
    String EXECUTE_MOUSE_ACTION = "EXECUTE_MOUSE_ACTION";
    String EXECUTE_KEY_PRESS = "EXECUTE_KEY_PRESS";

}
