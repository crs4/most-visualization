package it.crs4.most.visualization.utils.zmq;

import android.os.Handler;
import android.os.Message;

public class ECGSubscriber extends BaseSubscriber {

    public ECGSubscriber(String address, String topic, Handler handler) {
        super(address, topic, handler);
    }

    public ECGSubscriber(String protocol, String address, String port, String topic, Handler handler) {
        super(protocol, address, port, topic, handler);
    }

    public ECGSubscriber(String address, String topic) {
        super(address, topic);
    }

    public ECGSubscriber(String protocol, String address, String port, String topic) {
        super(protocol, address, port, topic);
    }

    @Override
    public void notifyMessage(String ecg) {
        Handler handler = getResponseHandler();
        if (handler != null) {
            Message message = handler.obtainMessage();
            String topic = getTopic();
            if (topic == null || !ecg.equals(topic)) {
                message.obj = Double.parseDouble(ecg);
                message.sendToTarget();
            }
        }
    }
}
