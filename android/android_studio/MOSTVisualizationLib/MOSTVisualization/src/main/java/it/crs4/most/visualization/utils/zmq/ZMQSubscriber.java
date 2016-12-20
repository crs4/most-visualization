package it.crs4.most.visualization.utils.zmq;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class ZMQSubscriber extends BaseSubscriber implements Runnable{
    private static String TAG = "ZMQSubscriber";
    public String address;
    private ZMQ.Context context;
    private ZMQ.Socket socket;
    private boolean pubIsAlive = true;
    private long lastKeepAlive;
    private Handler keepAliveHandler;
    private Runnable keepAliveRunnable;
    public static short KEEP_ALIVE_INTERVAL = 5000;
    //private ZContext CONTEXT = new ZContext();
    private boolean anyMessageReceived = false;
    private HandlerThread looperThread;
    //used to reconnect in case of no message receveid. It seems some reconnection problems exist
    // only in first stage of conection when wifi is unstable


    public ZMQSubscriber(String address) {
        this.address = address;
        context = ZMQ.context(1);
        socket = context.socket(ZMQ.SUB);
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


    private void connect(){
        socket.connect("tcp://" + address);
        Log.d(TAG, "connection to " + address);
        socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        socket.setReceiveTimeOut(1000);
        Log.d(TAG, "subscribed");
    }

    @Override
    public void run() {
        connect();
        long startTime = System.currentTimeMillis();
        long currentTime;

        while (true) {
            try {
                String msg = socket.recvStr(0);
                if (msg != null) {
                    Log.d(TAG, "message received: " + msg);
                    anyMessageReceived = true;
                    notifyMessage(msg);
                }
                else if(!anyMessageReceived) {

                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime > KEEP_ALIVE_INTERVAL){
                        Log.d(TAG, String.format("no message received after %d, reconnection...", KEEP_ALIVE_INTERVAL));
                        socket.disconnect("tcp://" + address);
                        connect();
                        startTime = currentTime;
                    }

                }
            }
            catch ( org.zeromq.ZMQException ex) {
                Log.d(TAG, "error in receiving message, probably keep alive failed");
            }

        }
    }
}
