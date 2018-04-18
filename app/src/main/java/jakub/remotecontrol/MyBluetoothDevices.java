package jakub.remotecontrol;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * Created by Jakub on 27.01.2018.
 */

public class MyBluetoothDevices {
     ArrayList<BluetoothDevice> devicesList = new ArrayList<BluetoothDevice>();
     ArrayList<String> devicesNames = new ArrayList<String>();


    public void addDevice(BluetoothDevice dev)
    {
        if(!devicesList.contains(dev)) {
            devicesList.add(dev);
            devicesNames.add(dev.getName());
        }
    }

    public void clearAll()
    {
        devicesList.clear();
        devicesNames.clear();

    }

    public  BluetoothDevice getDeviceByName(String devName)
    {
        for(BluetoothDevice dev : devicesList)
        {
            if(devName.equals(dev.getName())) return dev;
        }
        return null;
    }

    public ArrayList<String> getDevicesNames()
    {
        return this.devicesNames;
    }

}
