package jakub.remotecontrol

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var myDevicesList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val info = findViewById<TextView>(R.id.infoTextView)
        info.setText("Halo from the other side")


        //get listView object from xml
        val myDevicesListView = findViewById<ListView>(R.id.devicesListView)

        myDevicesList = arrayListOf("device1", "device2")//todo: get all bt devices

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, myDevicesList)

        if(myDevicesListView != null) {
            // Assign adapter to ListView
            myDevicesListView!!.setAdapter(adapter)
            // ListView Item Click Listener
            myDevicesListView!!.setOnItemClickListener(object : OnItemClickListener {

                override fun onItemClick(parent: AdapterView<*>, view: View,
                                         position: Int, id: Long) {

                    // ListView Clicked item index

                    // ListView Clicked item value
                    val itemValue = myDevicesListView!!.getItemAtPosition(position) as String

                    // Show Alert
                    Toast.makeText(applicationContext,
                            "Position :$position  ListItem : $itemValue", Toast.LENGTH_LONG)
                            .show()

                }

            })
        }


        connectBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                myDevicesList!!.add("hello test2")
                adapter.notifyDataSetChanged()


                Toast.makeText(applicationContext, " connect clicked", Toast.LENGTH_LONG)
                        .show()
            }
        })


        findMoreBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                // Code here executes on main thread after user presses button
                myDevicesList!!.remove("hello test2")
                adapter.notifyDataSetChanged()
                Toast.makeText(applicationContext, " findMore clicked", Toast.LENGTH_LONG)
                        .show()
            }
        })
/*
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        //setup bluetooth:
        setUpBluetooth()

        //display paired devices
        val values = emptyArray<String>()
        values.plusElement("test\ttest")

        val pairedDevices = queryForPairedDevices()
        if (pairedDevices.size > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices) {
                val deviceName = device.getName()
                val deviceHardwareAddress = device.getAddress() // MAC address
                values.plusElement(deviceName + "\t" + deviceHardwareAddress)
            }
        }

        val listView = findViewById<ListView>(R.id.devicesListView)
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values)
        listView.adapter = adapter
        */
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        //unregisterReceiver(mReceiver)
    }
    fun setUpBluetooth() {
        val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (myBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return
        }
        val REQUEST_ENABLE_BT = 123
        if (!myBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {
       override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
            }
        }
    }


    fun queryForPairedDevices() : Set<BluetoothDevice> {

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = mBluetoothAdapter.getBondedDevices()
        return pairedDevices

    }
}
