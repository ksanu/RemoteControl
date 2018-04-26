package jakub.remotecontrol;

/**
 * Created by Jakub on 20.04.2018.
 */

public interface MessageContent {
     interface AUTHORIZATION_RESULT{
        String SUCCESS = "SUCCESS";
        String FAILURE = "FAILURE";
    }

     interface STATE{
        String READY_FOR_REMOTE_CONTROL = "READY_FOR_REMOTE_CONTROL";
        String CLOSING = "CLOSING";
    }

    interface SINGLEACTION{
        String MOUSEUP =  "MOUSEUP";
        String MOUSEDOWN = "MOUSEDOWN";
        String MOUSELEFT = "MOUSELEFT";
        String MOUSERIGHT = "MOUSERIGHT";
        String MOUSERELEASED = "MOUSERELEASED";
        String MOUSEPRESSED = "MOUSEPRESSED";
        String KEYPRESSED = "KEYPRESSED";
        String KEYRELEASED = "KEYRELEASED";
        String RUNAPP = "RUNAPP";

    }
}
