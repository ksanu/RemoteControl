package jakub.remotecontrol;

import android.bluetooth.BluetoothDevice;


public class MyBluetooth {
    static MyBluetoothDevices btDev = new MyBluetoothDevices();
    static BluetoothClient btClient=null;
    static String encodedPwHash = null;

    public interface MessageConstants {
        int TO_ConnectActivity_CONNECTED = 10;
        int CONNECTION_ERROR = -11;

        int TO_AuthorizeActivity_Message = 20;
        int TO_AuthorizeActivity_Salt = 21;
        int TO_AuthorizeActivity_Authorization_Result = 22;

        int SERVER_STATE = 30;

        int TO_REMOTE_AVAILABLE_ACTION = 40;


    }

    public static void cancelBTClient()
    {
        if(btClient != null)
        {
            btClient.stopBTClient();
            btClient = null;
            encodedPwHash = null;
        }
    }
}
