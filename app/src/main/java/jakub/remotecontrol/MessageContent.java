package jakub.remotecontrol;

/**
 * Created by Jakub on 20.04.2018.
 */

public interface MessageContent {
    public interface AUTHORIZATION_RESULT{
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

    public interface STATE{
        String READY_FOR_REMOTE_CONTROL = "READY_FOR_REMOTE_CONTROL";
        String CLOSING = "CLOSING";
    }
}
