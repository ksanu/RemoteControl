package jakub.remotecontrol;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Jakub on 26.01.2018.
 */

public class BluetoothClient {
    private Integer btState = null;
    BufferedReader myBlueroothConnectionReader = null;
    PrintWriter myBluetoorhConnectionWriter = null;
    private  BluetoothSocket mmSocket = null;
    private  BluetoothDevice mmDevice = null;
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler;
    public BluetoothClient(Handler msgHandler){
        mHandler = msgHandler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
    }

    synchronized public void changeMsgHandler(Handler msgHandler)
    {
        mHandler = msgHandler;
    }

    /**
     * A method for sending messages to remote server.
     * @param messageType The type of the message.
     * @param messageContent The content of the message.
     * @return Rerurns true if sended succesfully. False otherwise.
     */
    public boolean sendMessage(String messageType, String messageContent)
    {
        if(btState.equals(BTStates.btConnectedWithDevice)&&myBluetoorhConnectionWriter!=null)
        {
            myBluetoorhConnectionWriter.write(messageType + "\t" + messageContent + "\n");
            myBluetoorhConnectionWriter.flush();
            return true;
        }else {
            return false;
        }
    }
    public void connectToServer(BluetoothDevice device)
    {
        new Thread(new ConnectThread(device)).start();
    }

    public void startCommunication()
    {
        new Thread(new ConnectedThread(mmSocket)).start();

    }
    public void startAunthorizationThread()
    {
        new Thread(new AuthorizationThread(mmSocket)).start();

    }
    public void stopBTClient()
    {
        btState = BTStates.btEND;
        try{
           if(mmSocket!=null) mmSocket.close();
           if(myBlueroothConnectionReader!=null) myBlueroothConnectionReader.close();
           if(myBluetoorhConnectionWriter!=null) myBluetoorhConnectionWriter.close();

           mmSocket = null;
           myBluetoorhConnectionWriter = null;
           myBlueroothConnectionReader = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            btState = BTStates.btConnecting;
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                java.util.UUID myUUID = java.util.UUID.fromString("08acfa82-0000-1000-8000-00805f9b34fb");//145554050
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                btState = BTStates.btUnableToConnect;
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    //Log.e(TAG, "Could not close the client socket", closeException);
                    closeException.printStackTrace();
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            if(btState.equals(BTStates.btConnecting)) {
                btState = BTStates.btConnectedWithDevice;
                Message msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.TO_ConnectActivity_CONNECTED);
                msg.sendToTarget();
            }else
            {
                Message msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.CONNECTION_ERROR);
                msg.sendToTarget();
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                btState = BTStates.btEND;
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openIOStreams(BluetoothSocket socket)
    {
        if(myBlueroothConnectionReader==null && myBluetoorhConnectionWriter==null) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            myBlueroothConnectionReader = new BufferedReader(new InputStreamReader(tmpIn));
            myBluetoorhConnectionWriter = new PrintWriter(new OutputStreamWriter(tmpOut));
        }
    }

    public void sendGetPasswordSalt()
    {
        sendMessage(MessageTypes.Client.GET_PASSWORD_SALT, "null");
    }
    public void sendAuthorizePasswordHash(String myPasswordHash)
    {
        sendMessage(MessageTypes.Client.AUTHORIZE_PASSWORD_HASH, myPasswordHash);
    }
    public void sendReadyForRemoteControl()
    {
        sendMessage(MessageTypes.Client.CLIENT_STATE, MessageContent.STATE.READY_FOR_REMOTE_CONTROL);
    }
    public void sendClosing()
    {
        sendMessage(MessageTypes.Client.CLIENT_STATE, MessageContent.STATE.CLOSING);
    }

    private class AuthorizationThread extends Thread{

        boolean stopThread = false;
        public AuthorizationThread(BluetoothSocket socket)
        {
            openIOStreams(socket);

        }

        public void stopAuthorizationThread()
        {
            stopThread = true;
        }
        public void run()
        {
            sendGetPasswordSalt();
            Message msg = null;
            // Keep listening to the InputStream until an exception occurs.
            while (!btState.equals(BTStates.btEND)&&!stopThread) {
                try {
                    // Read message line from the InputStream.
                    String messageLine = myBlueroothConnectionReader.readLine();
                    String[] msgTypeAndContent = messageLine.split("\\t");
                    switch(msgTypeAndContent[0])
                    {
                        case MessageTypes.Server.PASSWORD_SALT:
                            //todo: zapisać ?
                            msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.TO_AuthorizeActivity_Salt, msgTypeAndContent[1]);
                            msg.sendToTarget();
                            break;
                        case MessageTypes.Server.AUTHORIZATION_RESULT:
                            msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.TO_AuthorizeActivity_Authorization_Result, msgTypeAndContent[1]);
                            msg.sendToTarget();
                            break;
                        case MessageTypes.Server.SERVER_STATE:
                            msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.SERVER_STATE, msgTypeAndContent[1]);
                            msg.sendToTarget();
                            break;
                        default:
                            break;

                    }
                } catch (IOException e) {
                    btState = BTStates.btEND;
                    msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.CONNECTION_ERROR);
                    msg.sendToTarget();
                    break;
                }
            }
        }

    }
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            openIOStreams(socket);

        }

        public void run() {

            // Keep listening to the InputStream until an exception occurs.
            while (!btState.equals(BTStates.btEND)) {
                try {
                    // Read message line from the InputStream.
                    String messageLine = myBlueroothConnectionReader.readLine();
                    //todo: handle received message
                    Message msg = mHandler.obtainMessage(MyBluetooth.MessageConstants.TO_AuthorizeActivity_Message, messageLine);
                    msg.sendToTarget();
                    System.out.println(messageLine);
                } catch (IOException e) {
                    btState = BTStates.btEND;
                    break;
                }
            }
        }


        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            btState = BTStates.btEND;
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }




}

