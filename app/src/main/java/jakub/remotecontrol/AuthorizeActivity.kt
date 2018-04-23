package jakub.remotecontrol

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.*
import jakub.remotecontrol.Security.PasswordHandler

class AuthorizeActivity : AppCompatActivity() {

    var passSalt:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)

        Toast.makeText(applicationContext,
                "Połączono!", Toast.LENGTH_LONG)
                .show()

        val mHandler = object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                //super.handleMessage(msg)
                var receivedString: String
                when(msg!!.what)
                {
                    MyBluetooth.MessageConstants.TO_AuthorizeActivity_Message -> {
                        receivedString = msg.obj as String
                        Toast.makeText(applicationContext, receivedString, Toast.LENGTH_LONG).show()
                    }
                    MyBluetooth.MessageConstants.CONNECTION_ERROR -> finish()

                    MyBluetooth.MessageConstants.TO_AuthorizeActivity_Salt ->{
                        receivedString = msg.obj as String
                        passSalt = receivedString
                        val loginBtn = findViewById<Button>(R.id.loginBtn)
                        loginBtn.isEnabled = true;
                    }
                    MyBluetooth.MessageConstants.TO_AuthorizeActivity_Authorization_Result ->{
                        receivedString = msg.obj as String
                        if(receivedString.equals(MessageContent.AUTHORIZATION_RESULT.SUCCESS))
                        {
                            Toast.makeText(applicationContext, "Zalogowano!", Toast.LENGTH_LONG).show()
                            MyBluetooth.btClient.sendReadyForRemoteControl()
                        }else if(receivedString.equals((MessageContent.AUTHORIZATION_RESULT.FAILURE))){
                            setLoading(false)
                            Toast.makeText(applicationContext, "Błędne hasło", Toast.LENGTH_SHORT).show()
                        }
                    }
                    MyBluetooth.MessageConstants.SERVER_STATE -> {
                        receivedString = msg.obj as String
                        if(receivedString.equals(MessageContent.STATE.READY_FOR_REMOTE_CONTROL))
                            startRemoteActivity()
                        else if(receivedString.equals(MessageContent.STATE.CLOSING))
                            finish()
                    }
                }
            }
        }
        if(MyBluetooth.btClient != null){
            MyBluetooth.btClient.changeMsgHandler(mHandler)
            MyBluetooth.btClient.startAunthorizationThread()
        }else{
            Toast.makeText(applicationContext, "Błąd połączenia", Toast.LENGTH_LONG).show()
            finish()
        }

    }


    fun startRemoteActivity()
    {
        val intent = Intent(this, RemoteActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun setLoading(boolean: Boolean)
    {
        val progress = findViewById<ProgressBar>(R.id.progressBar)

        if(boolean) progress.visibility = View.VISIBLE
        else progress.visibility = View.INVISIBLE

        val loginBtn = findViewById<TextView>(R.id.loginBtn)
        loginBtn.isEnabled = !boolean

        val pwText = findViewById<EditText>(R.id.passwordEditText)
        pwText.isEnabled = !boolean
    }

    fun loginBtnOnClick(view: View)
    {
        if(passSalt!="") {
            setLoading(true);
            Thread(Runnable(){
                kotlin.run {
                    val pwText = findViewById<EditText>(R.id.passwordEditText)
                    val password = pwText.text.toString()
                    val pwHash = PasswordHandler.getSaltedHash(password, passSalt)
                    MyBluetooth.encodedPwHash = pwHash
                    MyBluetooth.btClient.sendAuthorizePasswordHash(pwHash)
                }
            }).start()

        }else{
            Toast.makeText(applicationContext, "Waiting for salt..", Toast.LENGTH_LONG).show()
        }
    }

}
