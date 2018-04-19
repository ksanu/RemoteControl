package jakub.remotecontrol

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast

class ConnectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        //Get selected server device
        val selectedBTDevName = intent.getStringExtra(EXTRA_BTDEVICE)
        val selectedBTDev = MyBluetooth.btDev.getDeviceByName(selectedBTDevName)
        val itemValue = selectedBTDev.name
        Toast.makeText(applicationContext,
                "BTServer : $itemValue", Toast.LENGTH_LONG)
                .show()

        val mHandler = object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                //super.handleMessage(msg)
                when(msg!!.what)
                {
                    MyBluetooth.MessageConstants.TO_ConnectActivity_CONNECTED -> startAuthorizeActicity()
                    MyBluetooth.MessageConstants.TO_ConnectActivity_CONNECTION_ERROR -> finish()
                }
            }
        }
        val btClient = BluetoothClient(mHandler)
        MyBluetooth.cancelBTClient()
        MyBluetooth.btClient = btClient
        MyBluetooth.btClient.connectToServer(selectedBTDev)


    }

    fun startAuthorizeActicity()
    {
        val intent = Intent(this, AuthorizeActivity::class.java)
        startActivity(intent)
    }



}
