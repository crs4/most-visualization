package it.crs4.most.visualization.utils.zmq;

import android.opengl.Visibility;
import android.os.AsyncTask;
import android.util.Log;

import org.zeromq.ZMQ;

import java.io.Serializable;

public class ZMQPublisher implements Runnable, IPublisher, Serializable {

    private static String TAG = "ZMQPublisher";
    public int port = 5555;
    private ZMQ.Context context;
    private ZMQ.Socket publisher;

    public ZMQPublisher() {

    }

    public ZMQPublisher(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:" + String.valueOf(port));
        Log.d(TAG, "publisher binded to port " + String.valueOf(port));

    }

    public void send(String msg) {
        new SendMessage().execute(msg);
    }

    public void close() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                publisher.close();
                context.close();
                Log.d(TAG, "closed zmq context");
                return null;
            }
        }.execute();

    }

    protected class SendMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... messages) {
            try{
                for (String msg : messages) {
                    Log.d(TAG, "sending msg " + msg);
                    publisher.send(msg);
                }
            }
            catch (org.zeromq.ZMQException ex) {
                ex.printStackTrace();
            }

            return null;
        }
    }
}
