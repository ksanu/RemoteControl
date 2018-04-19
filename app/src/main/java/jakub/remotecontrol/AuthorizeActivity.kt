package jakub.remotecontrol

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.*

class AuthorizeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)

        Toast.makeText(applicationContext,
                "Połączono!", Toast.LENGTH_LONG)
                .show()

        val mHandler = object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                //super.handleMessage(msg)
                when(msg!!.what)
                {
                    MyBluetooth.MessageConstants.TO_AuthorizeActivity_Message -> {
                        val receivedString = msg.obj as String
                        Toast.makeText(applicationContext, receivedString, Toast.LENGTH_LONG).show()
                    }
                    MyBluetooth.MessageConstants.TO_ConnectActivity_CONNECTION_ERROR -> finish()
                }
            }
        }
        if(MyBluetooth.btClient != null){
            MyBluetooth.btClient.changeMsgHandler(mHandler)
            MyBluetooth.btClient.startCommunication()
        }else{
            Toast.makeText(applicationContext, "Błąd połączenia", Toast.LENGTH_LONG).show()
        }
    }

    fun loginBtnOnClick(view: View)
    {
        view.isEnabled = false
        //val loginBtn = findViewById<TextView>(R.id.loginBtn)
        //loginBtn.isEnabled = false
        val progress = findViewById<ProgressBar>(R.id.progressBar)
        progress.visibility = View.VISIBLE
        val pwText = findViewById<EditText>(R.id.passwordEditText)
        pwText.isEnabled = false
    }

}
