package it.crs4.most.visualization.utils.zmq;

import android.util.Log;

import org.zeromq.ZMQ;

public class ZMQSubscriber extends BaseSubscriber implements Runnable{
    private ZMQ.Context context;
    private ZMQ.Socket socket;
    private static String TAG ="ZMQSubscriber";
    public String address;

    public ZMQSubscriber(String address){
        this.address = address;
    }

    @Override
    public void run() {
        context = ZMQ.context(1);
        socket= context.socket(ZMQ.SUB);
        socket.connect("tcp://" + address);
        socket.subscribe(ZMQ.SUBSCRIPTION_ALL);

        while (true) {
            String msg = socket.recvStr(0);
            Log.d(TAG, "message received: " + msg);
            notifyMessage(msg);
        }
    }
}
