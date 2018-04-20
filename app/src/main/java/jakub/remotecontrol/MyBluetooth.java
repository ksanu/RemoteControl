package jakub.remotecontrol;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Jakub on 18.04.2018.
 */

public class MyBluetooth {
    static MyBluetoothDevices btDev = new MyBluetoothDevices();
    static BluetoothClient btClient=null;
    // Defines a Handler object that's attached to the UI thread

    public interface MessageConstants {
        int TO_ConnectActivity_CONNECTED = 10;
        int CONNECTION_ERROR = -11;

        int TO_AuthorizeActivity_Message = 20;
        int TO_AuthorizeActivity_Salt = 21;
        int TO_AuthorizeActivity_Authorization_Result = 22;

        int SERVER_STATE = 30;


    }

    public static void cancelBTClient()
    {
        if(btClient != null)
        {
            btClient.stopBTClient();
            btClient = null;
        }
    }
}
