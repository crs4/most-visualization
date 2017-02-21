package it.crs4.most.visualization.utils.zmq;

import android.os.Handler;
import android.util.Log;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public abstract class BaseSubscriber implements Runnable {
    protected Handler mHandler;
    private static String TAG = "BaseSubscriber";
    private static short KEEP_ALIVE_INTERVAL = 5000;
    private String mUrl;
    private String mTopic;
    private Context mContext;
    private Socket socket;
    private boolean anyMessageReceived = false;
    private boolean toClose = false;
    //used to reconnect in case of no message receveid. It seems some reconnection problems exist
    // only in first stage of conection when wifi is unstable


    public BaseSubscriber(String url, String topic) {
        mUrl = url;
        mTopic = topic;
    }

    public BaseSubscriber(String protocol, String address, String port, String topic) {
        this(String.format("%1$s://%2$s:%3$s", protocol, address, port), topic);
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String topic) {
        mTopic = topic;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }


    protected void connect() {
        mContext = ZMQ.context(1);
        socket = mContext.socket(ZMQ.SUB);
        socket.connect(mUrl);
        Log.d(TAG, "Connection to " + mUrl);
        if (mTopic != null) {
            socket.subscribe(mTopic.getBytes());
        }
        else {
            socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        }
        socket.setReceiveTimeOut(1000);
        Log.d(TAG, "Subscribed");
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
                    anyMessageReceived = true;
                    notifyMessage(msg);
                }
                else if (!anyMessageReceived) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime > KEEP_ALIVE_INTERVAL) {
                        Log.d(TAG, String.format("no message received after %d, reconnection...", KEEP_ALIVE_INTERVAL));
                        socket.disconnect(mUrl);
                        socket.close();
                        mContext.close();
                        connect();
                        startTime = currentTime;
                    }
                }
            }
            catch (org.zeromq.ZMQException ex) {
                Log.d(TAG, "Error receiving message, probably keep alive failed");
            }
        }
        mContext.close();
    }

    protected abstract void notifyMessage(String msg);

    public void close() {
        toClose = true;
    }
}
