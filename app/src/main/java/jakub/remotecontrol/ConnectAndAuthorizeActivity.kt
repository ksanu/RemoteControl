package jakub.remotecontrol

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

class ConnectAndAuthorizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_and_authorize)

        //Get selected server device
        val selectedBTDevName = intent.getStringExtra(EXTRA_BTDEVICE)
        val selectedBTDev = MyBluetooth.btDev.getDeviceByName(selectedBTDevName)
        val itemValue = selectedBTDev.name
        Toast.makeText(applicationContext,
                "BTServer : $itemValue", Toast.LENGTH_LONG)
                .show()
        val textView = findViewById<TextView>(R.id.messageText)
        val btClient = BluetoothClient()
        MyBluetooth.btClient = btClient
        MyBluetooth.btClient.connectToServer(selectedBTDev)
    }

    override fun onDestroy() {
        super.onDestroy()
        MyBluetooth.btClient.stopBTClient()
        MyBluetooth.btClient = null;
    }
}
