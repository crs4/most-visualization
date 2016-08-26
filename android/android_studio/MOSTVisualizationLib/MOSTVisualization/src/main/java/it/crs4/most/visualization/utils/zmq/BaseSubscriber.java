package it.crs4.most.visualization.utils.zmq;


import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseSubscriber {
    public Handler handler;

    public void notifyMessage(String msg) {
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