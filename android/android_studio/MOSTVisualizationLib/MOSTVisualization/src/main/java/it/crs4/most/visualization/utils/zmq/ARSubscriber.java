package it.crs4.most.visualization.utils.zmq;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class ARSubscriber extends BaseSubscriber {

    public ARSubscriber(String address, String topic, Handler handler) {
        super(address, topic, handler);
    }

    public ARSubscriber(String protocol, String address, String port, String topic, Handler handler) {
        super(protocol, address, port, topic, handler);
    }

    public ARSubscriber(String address, String topic) {
        super(address, topic);
    }

    public ARSubscriber(String protocol, String address, String port, String topic) {
        super(protocol, address, port, topic);
    }

    public void notifyMessage(String msg) {
        Handler handler = getResponseHandler();
        if (handler != null) {
            Message message = handler.obtainMessage();
            try {
                message.obj = new JSONObject(msg);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            message.sendToTarget();
        }
    }
}