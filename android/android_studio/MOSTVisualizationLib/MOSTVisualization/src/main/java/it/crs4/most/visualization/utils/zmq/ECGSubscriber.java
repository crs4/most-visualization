package it.crs4.most.visualization.utils.zmq;

import android.os.Message;

public class ECGSubscriber extends ZMQSubscriber {

    public ECGSubscriber(String address, String topic) {
        super(address, topic);
    }

    @Override
    public void notifyMessage(String ecg) {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            String topic = getTopic();
            if (topic == null || !ecg.equals(topic)) {
                message.obj = Double.parseDouble(ecg);
                message.sendToTarget();
            }
        }
    }
}
