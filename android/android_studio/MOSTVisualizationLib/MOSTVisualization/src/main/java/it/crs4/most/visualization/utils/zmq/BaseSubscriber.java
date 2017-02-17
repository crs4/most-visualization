package it.crs4.most.visualization.utils.zmq;


import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseSubscriber {
    protected Handler mHandler;

    public void notifyMessage(String msg) {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            try {
                message.obj = new JSONObject(msg);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            message.sendToTarget();
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}