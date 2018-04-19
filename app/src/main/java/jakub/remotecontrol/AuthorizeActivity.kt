package jakub.remotecontrol

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.TextView
import android.widget.Toast

class AuthorizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)

        Toast.makeText(applicationContext,
                "Połączono!", Toast.LENGTH_LONG)
                .show()
        val textView = findViewById<TextView>(R.id.messageText)

        val mHandler = object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                //super.handleMessage(msg)
                when(msg!!.what)
                {
                    MyBluetooth.MessageConstants.TO_AuthorizeActivity_Message -> {
                        val receivedString = msg!!.obj as String
                        Toast.makeText(applicationContext, receivedString, Toast.LENGTH_LONG).show()
                        textView.setText(receivedString)
                    }
                    MyBluetooth.MessageConstants.TO_ConnectActivity_CONNECTION_ERROR -> finish()
                }
            }
        }
        if(MyBluetooth.btClient != null){
            MyBluetooth.btClient.changeMsgHandler(mHandler)
            MyBluetooth.btClient.startCommunication()
        }else{
            textView.setText("Błąd połączenia")
        }
    }


}
