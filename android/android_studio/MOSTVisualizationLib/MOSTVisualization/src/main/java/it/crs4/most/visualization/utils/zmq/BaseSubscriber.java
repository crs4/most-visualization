package it.crs4.most.visualization.utils.zmq;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.zeromq.ZMQ;

public abstract class BaseSubscriber extends HandlerThread {
    private final static String TAG = "BaseSubscriber";
    private final static short KEEP_ALIVE_INTERVAL = 5000;
    private String mUrl;
    private String mTopic;
    private ZMQ.Context mContext;
    private ZMQ.Socket mSocket;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private boolean messageReceived = false;
    private boolean close = false;
    //used to reconnect in case of no message receveid. It seems some reconnection problems exist
    // only in first stage of conection when wifi is unstable

    BaseSubscriber(String url, String topic) {
        super(TAG);
        mUrl = url;
        mTopic = topic;
    }

    BaseSubscriber(String protocol, String address, String port, String topic) {
        this(String.format("%1$s://%2$s:%3$s", protocol, address, port), topic);
    }

    BaseSubscriber(String url, String topic, Handler responseHandler) {
        super(TAG);
        mUrl = url;
        mTopic = topic;
        mResponseHandler = responseHandler;
    }

    BaseSubscriber(String protocol, String address, String port, String topic, Handler responseHandler) {
        this(String.format("%1$s://%2$s:%3$s", protocol, address, port), topic, responseHandler);
    }

    public void prepareResponseHandler() {
        mRequestHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                getECGValues();
            }
        };
    }
    public void startReceiving() {
        mRequestHandler.obtainMessage().sendToTarget();
    }

    public void stopReceiving() {
        close();
    }

    private void connect() {
        mContext = ZMQ.context(1);
        mSocket = mContext.socket(ZMQ.SUB);
        mSocket.connect(mUrl);
        Log.d(TAG, "Connecting to " + mUrl);
        if (mTopic != null) {
            mSocket.subscribe(mTopic.getBytes());
        }
        else {
            mSocket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        }
        mSocket.setReceiveTimeOut(1000);
        Log.d(TAG, "Subscribed");
    }

    private void disconnect() {
        mSocket.disconnect(mUrl);
        mSocket.close();
        mContext.close();
    }

    private void getECGValues() {
        connect();
        close = false;
        long startTime = System.currentTimeMillis();
        long currentTime;

        while (!close) {
            try {
                String msg = mSocket.recvStr();
                if (msg != null) {
                    messageReceived = true;
                    notifyMessage(msg);
                }
                else if (!messageReceived) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime > KEEP_ALIVE_INTERVAL) {
                        Log.d(TAG, String.format("no message received after %d, reconnection...", KEEP_ALIVE_INTERVAL));
                        disconnect();
                        connect();
                        startTime = currentTime;
                    }
                }
            }
            catch (org.zeromq.ZMQException ex) {
                Log.d(TAG, "error in receiving message, probably keep alive failed");
            }
        }
        disconnect();
    }

    @Override
    public boolean quit() {
        close();
        return super.quit();
    }

    protected abstract void notifyMessage(String msg);

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

    public Handler getResponseHandler() {
        return mResponseHandler;
    }

    public void setResponseHandler(Handler responseHandler) {
        this.mResponseHandler = responseHandler;
    }

    public void close() {
        close = true;
    }
}
