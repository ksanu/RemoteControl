package jakub.remotecontrol;

/**
 * Created by Jakub on 19.04.2018.
 */

public interface MessageTypes {

    public interface Server {
        //sent by server:
        String PASSWORD_SALT = "PASSWORD_SALT";
        String AUTHORIZATION_RESULT = "AUTHORIZATION_RESULT";
        String AVAILABLE_ACTION = "AVAILABLE_ACTION";
        String SERVER_STATE = "SERVER_STATE";//??
    }
    public interface Client {
        //sent by client:
        String GET_PASSWORD_SALT = "GET_PASSWORD_SALT";
        String AUTHORIZE_PASSWORD_HASH = "AUTHORIZE_PASSWORD_HASH";
        String GET_AVAILABLE_ACTIONS = "GET_AVAILABLE_ACTIONS";
        String CLIENT_STATE = "CLIENT_STATE";//??
        String EXECUTE_ACTION = "RUN_ACTION";
        String EXECUTE_MOUSE_ACTION = "EXECUTE_MOUSE_ACTION";
        String EXECUTE_KEY_PRESS = "EXECUTE_KEY_PRESS";
    }
}
