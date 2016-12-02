package it.crs4.most.visualization.utils.zmq;

import android.text.style.TextAppearanceSpan;
import android.util.Log;

import org.zeromq.ZMQ;

public class ZMQSubscriber extends BaseSubscriber implements Runnable {
    private static String TAG = "ZMQSubscriber";
    public String address;
    private ZMQ.Context context;
    private ZMQ.Socket socket;

    public ZMQSubscriber(String address) {
        this.address = address;
    }

    @Override
    public void run() {
        context = ZMQ.context(1);
        socket = context.socket(ZMQ.SUB);
        socket.connect("tcp://" + address);
        Log.d(TAG, "connection to " + address);
        socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        Log.d(TAG, "subscribed");

        while (true) {
            String msg = socket.recvStr(0);
            Log.d(TAG, "message received: " + msg);
            notifyMessage(msg);
        }
    }
}
