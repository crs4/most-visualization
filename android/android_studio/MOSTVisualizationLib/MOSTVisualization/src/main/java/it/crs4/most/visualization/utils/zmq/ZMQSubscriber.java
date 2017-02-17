package it.crs4.most.visualization.utils.zmq;

import android.util.Log;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class ZMQSubscriber extends BaseSubscriber implements Runnable {
    private static String TAG = "ZMQSubscriber";
    public static short KEEP_ALIVE_INTERVAL = 5000;
    private String mAddress;
    private String mTopic;
    private Context mContext;
    private Socket socket;
    private boolean pubIsAlive = true;
    private long lastKeepAlive;
    private boolean anyMessageReceived = false;
    private boolean toClose = false;
    //used to reconnect in case of no message receveid. It seems some reconnection problems exist
    // only in first stage of conection when wifi is unstable


    public ZMQSubscriber(String address, String topic) {
        mAddress = "tcp://" + address;
        mTopic = topic;
    }

    public boolean isPubIsAlive() {
        return pubIsAlive;
    }

    public void setPubIsAlive(boolean pubIsAlive) {
        this.pubIsAlive = pubIsAlive;
    }

    public long getLastKeepAlive() {
        return lastKeepAlive;
    }

    public void setLastKeepAlive(long lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
    }


    protected void connect() {
        mContext = ZMQ.context(1);
        socket = mContext.socket(ZMQ.SUB);
        socket.connect(mAddress);
        Log.d(TAG, "connection to " + mAddress);
        if (mTopic != null) {
            socket.subscribe(mTopic.getBytes());
        }
        else {
            socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        }
        socket.setReceiveTimeOut(1000);
        Log.d(TAG, "subscribed");
    }

    @Override
    public void run() {
        connect();
        long startTime = System.currentTimeMillis();
        long currentTime;

        while (!toClose) {
            try {
                String msg = socket.recvStr(0);
                if (msg != null) {
                    Log.d(TAG, "message received: " + msg);
                    anyMessageReceived = true;
                    notifyMessage(msg);
                }
                else if (!anyMessageReceived) {

                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime > KEEP_ALIVE_INTERVAL) {
                        Log.d(TAG, String.format("no message received after %d, reconnection...", KEEP_ALIVE_INTERVAL));
                        socket.disconnect(mAddress);
                        socket.close();
                        mContext.close();
                        connect();
                        startTime = currentTime;
                    }

                }
            }
            catch (org.zeromq.ZMQException ex) {
                Log.d(TAG, "error in receiving message, probably keep alive failed");
            }

        }
        mContext.close();
    }

    public void close() {
        toClose = true;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String topic) {
        mTopic = topic;
    }
}
