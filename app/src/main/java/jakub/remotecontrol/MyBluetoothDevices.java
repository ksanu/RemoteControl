package jakub.remotecontrol;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;



public class MyBluetoothDevices {
     private ArrayList<BluetoothDevice> devicesList = new ArrayList<>();
     private ArrayList<String> devicesNames = new ArrayList<>();


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
