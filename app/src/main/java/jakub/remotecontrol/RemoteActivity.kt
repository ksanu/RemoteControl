package jakub.remotecontrol

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.view.MotionEvent
import android.R.attr.button






class RemoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)

        val spinner = findViewById<Spinner>(R.id.availableActionsSpinner)
        val adapterForSpinner = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterForSpinner

        val lclickBtn = findViewById<Button>(R.id.leftClickBtn)
        lclickBtn.setOnTouchListener(View.OnTouchListener({
            view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                val content = MessageContent.SINGLEACTION.MOUSEPRESSED + "\t" + 1
                MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)

            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                // Released
                val content = MessageContent.SINGLEACTION.MOUSERELEASED + "\t" + 1
                MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)
            }
            true
        }))

        val rclickBtn = findViewById<Button>(R.id.rightClickBtn)
        rclickBtn.setOnTouchListener(View.OnTouchListener({
            view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // Pressed
                val content = MessageContent.SINGLEACTION.MOUSEPRESSED + "\t" + 3
                MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)

            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                // Released
                val content = MessageContent.SINGLEACTION.MOUSERELEASED + "\t" + 3
                MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)
            }
            true
        }))


/*
        val toSentEditText = findViewById<EditText>(R.id.toSentEditText)

        toSentEditText.addTextChangedListener(object : TextWatcher {
            var count:Int = 0
            var before:Int = 0
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editableText: Editable?) {
                var i = count.minus(before)
                var index = before;
                while(i>0)
                {
                    val keyCode = editableText!!.get(index).toInt()
                    var content = MessageContent.SINGLEACTION.KEYPRESSED + "\t" + keyCode
                    MyBluetooth.btClient.sendExecuteSingleAction(content)
                    content = MessageContent.SINGLEACTION.KEYRELEASED + "\t" + keyCode
                    MyBluetooth.btClient.sendExecuteSingleAction(content)
                    i=i-1
                    index = index + 1
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this.count = count
                this.before = before
            }
        })

        toSentEditText.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(v:TextView, actionId: Int, keyEvent:KeyEvent) : Boolean {
                if(keyEvent!!.action == KeyEvent.ACTION_DOWN) {
                    val unicodeValue = keyEvent.getUnicodeChar()
                    val content = MessageContent.SINGLEACTION.KEYPRESSED + "\t" + unicodeValue.toInt()
                    MyBluetooth.btClient.sendExecuteSingleAction(content)
                }else if(keyEvent!!.action == KeyEvent.ACTION_UP) {
                    val unicodeValue = keyEvent.getUnicodeChar()
                    val content = MessageContent.SINGLEACTION.KEYRELEASED + "\t" + unicodeValue.toInt()
                    MyBluetooth.btClient.sendExecuteSingleAction(content)
                }

                return false;
            }


        })
*/
        val mHandler = object: Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message?) {
                //super.handleMessage(msg)
                var receivedString: String
                when(msg!!.what)
                {
                    MyBluetooth.MessageConstants.CONNECTION_ERROR -> finish()

                    MyBluetooth.MessageConstants.TO_REMOTE_AVAILABLE_ACTION ->{
                        receivedString = msg.obj as String
                        adapterForSpinner.add(receivedString)
                        adapterForSpinner.notifyDataSetChanged()
                    }

                    MyBluetooth.MessageConstants.SERVER_STATE -> {
                        receivedString = msg.obj as String
                        if(receivedString.equals(MessageContent.STATE.CLOSING))
                            finish()
                    }
                }
            }
        }

        MyBluetooth.btClient.changeMsgHandler(mHandler)
        MyBluetooth.btClient.sendGetAvailableActions()

    }

    fun executeActionBtnOnClick(view: View)
    {
        val spinner = findViewById<Spinner>(R.id.availableActionsSpinner)
        val action = spinner.selectedItem as String
        MyBluetooth.btClient.sendExecuteAction(action)
        Toast.makeText(applicationContext, "Wykonywanie akcji...", Toast.LENGTH_SHORT).show()


    }

    fun SendBtnOnClick(view: View)
    {
        val toSentEditText = findViewById<EditText>(R.id.toSentEditText)
        val text = toSentEditText.text.toString()
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.TEXT_TO_TYPE, text)
    }

    fun upArrowBtnOnClick(view: View)
    {
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_MOUSE_ACTION, MessageContent.SINGLEACTION.MOUSEUP)
    }

    fun downArrowBtnOnClick(view: View)
    {
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_MOUSE_ACTION, MessageContent.SINGLEACTION.MOUSEDOWN)

    }
    fun leftArrowBtnOnClick(view: View)
    {
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_MOUSE_ACTION, MessageContent.SINGLEACTION.MOUSELEFT)

    }
    fun rightArrowBtnOnClick(view: View)
    {
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_MOUSE_ACTION, MessageContent.SINGLEACTION.MOUSERIGHT)

    }

    fun rightClickBtnOnClick(view: View)
    {
        var content = MessageContent.SINGLEACTION.MOUSEPRESSED + "\t" + 3
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)
        content = MessageContent.SINGLEACTION.MOUSERELEASED + "\t" + 3
        MyBluetooth.btClient.sendMessage(MessageTypes.Client.EXECUTE_SINGLE_ACTION, content)
    }
/*
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val unicodeValue = event!!.getUnicodeChar()
        val content = MessageContent.SINGLEACTION.KEYPRESSED + "\t" + unicodeValue.toInt()
        return super.onKeyDown(keyCode, event)
    }
    */

}
