package it.crs4.most.visualization.utils.zmq;

import android.os.Message;

public class ECGSubscriber extends BaseSubscriber {

    public ECGSubscriber(String address, String topic) {
        super(address, topic);
    }

    public ECGSubscriber(String protocol, String address, String port, String topic) {
        super(protocol, address, port, topic);
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
