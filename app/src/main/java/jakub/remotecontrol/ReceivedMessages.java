package jakub.remotecontrol;

import java.util.ArrayList;

/**
 * Created by Jakub on 26.01.2018.
 */

public class ReceivedMessages {
    ArrayList<String> messages = new ArrayList<>();

    public void addMessage(String msg)
    {
        //Dodaje na końcu listy
        messages.add(msg);
    }

    public String getMessage()
    {
        if(!messages.isEmpty()) {
            //zwraca pierwszą otrzymaną wiadomość na liście
            String s = messages.get(0);
            messages.remove(0);
            return s;
        }else{
            return null;
        }

    }

}
