package jakub.remotecontrol

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.action


const val EXTRA_BTDEVICE = "jakub.remotecontrol.BTDEVICE"

class MainActivity : AppCompatActivity() {

    val myDevices: MyBluetoothDevices = MyBluetooth.btDev
    var adapter : ArrayAdapter<String>? = null
    val REQUEST_ENABLE_BT = 1002


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        val filter2 = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mBluetoothStateChangedReveiver, filter2)

        val info = findViewById<TextView>(R.id.infoTextView)
        info.setText("Halo from the other side")

        //get listView object from xml
        val myDevicesListView = findViewById<ListView>(R.id.devicesListView)

        //setup bluetooth:
        val isBT =  setUpBluetooth()
        if(isBT) {

            //get the paired devices and add to the list
            val pairedDevices = queryForPairedDevices()
            if (pairedDevices.isNotEmpty()) {
                for (dev: BluetoothDevice in pairedDevices) {
                    myDevices.addDevice(dev)
                }
            } else {
                // nie ma sparowanych urządzeń
                //włącz discovery
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                val isStarted = mBluetoothAdapter.startDiscovery()
                if(isStarted) Toast.makeText(applicationContext, "Szukam urządzeń...", Toast.LENGTH_LONG)
                        .show()
                else Toast.makeText(applicationContext, "Błąd, nie można rozpocząć szukania", Toast.LENGTH_LONG)
                        .show()
            }

        }
        val myDevicesList = myDevices.getDevicesNames()//arrayListOf("device1", "device2")//todo: get all bt devices

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, myDevicesList)

        if(myDevicesListView != null) {
            // Assign adapter to ListView
            myDevicesListView.setAdapter(adapter)
            // ListView Item Click Listener
            myDevicesListView.onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>, view: View,
                                         position: Int, id: Long) {
                    // ListView Clicked item value
                    val itemValue = myDevicesListView!!.getItemAtPosition(position) as String
                    connectToServer(itemValue)
                    // Show Alert
                    Toast.makeText(applicationContext,
                            "Position :$position  ListItem : $itemValue", Toast.LENGTH_LONG)
                            .show()

                }

            }
        }


        connectBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {


                Toast.makeText(applicationContext, " connect clicked", Toast.LENGTH_LONG)
                        .show()
            }
        })


        findMoreBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                // Code here executes on main thread after user presses button
                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (mBluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Toast.makeText(applicationContext, "Urządzenie nie wspiera BT", Toast.LENGTH_LONG)
                            .show()
                }else
                {
                    val isStarted = mBluetoothAdapter.startDiscovery()
                    if(isStarted) Toast.makeText(applicationContext, "Szukam urządzeń...", Toast.LENGTH_LONG)
                            .show()
                    else Toast.makeText(applicationContext, "Błąd, nie można rozpocząć szukania", Toast.LENGTH_LONG)
                            .show()
                }
                adapter!!.notifyDataSetChanged()

                Toast.makeText(applicationContext, " findMore clicked", Toast.LENGTH_LONG)
                        .show()
            }
        })








    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver)
        unregisterReceiver(mBluetoothStateChangedReveiver)
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
    }

    /**
     * This method starts a new activity for connecting to server and authorization.
     * @param selectedBTDevName The name of the selected server device.
     */
    fun connectToServer(selectedBTDevName: String)
    {
        val intent = Intent(this, ConnectAndAuthorizeActivity::class.java).apply {
            putExtra(EXTRA_BTDEVICE, selectedBTDevName)
        }
        startActivity(intent)
    }

    /**
     * @return
     * true - if Bluetooth was set up succesfully
     * false otherwise.
     */
    fun setUpBluetooth(): Boolean {
        val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (myBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Urządzenie nie wspiera Bluetooth", Toast.LENGTH_LONG)
                    .show()
            return false
        }
        if (!myBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        return true
    }

    fun queryForPairedDevices(): Set<BluetoothDevice> {

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = mBluetoothAdapter.getBondedDevices()
        return pairedDevices

    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                myDevices.addDevice(device)
                adapter!!.notifyDataSetChanged()
                //val deviceName = device.name
                //val deviceHardwareAddress = device.address // MAC address
            }

        }
    }

    private val mBluetoothStateChangedReveiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED)
            {
                val bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                if (bluetoothState == BluetoothAdapter.STATE_ON)
                {
                    //Bluethooth is on, now you can perform your tasks
                    //get the paired devices and add to the list
                    val pairedDevices = queryForPairedDevices()
                    if (pairedDevices.isNotEmpty())
                    {
                        for (dev: BluetoothDevice in pairedDevices)
                        {
                            myDevices.addDevice(dev)

                        }
                        adapter!!.notifyDataSetChanged()
                    }
                }

                if (bluetoothState == BluetoothAdapter.STATE_OFF)
                {
                    myDevices.clearAll()
                    adapter!!.notifyDataSetChanged()
                }

            }
        }
    }







}

