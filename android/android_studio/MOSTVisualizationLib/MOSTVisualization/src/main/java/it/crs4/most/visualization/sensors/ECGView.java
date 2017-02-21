package it.crs4.most.visualization.sensors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import it.crs4.most.visualization.utils.zmq.ECGSubscriber;


public class ECGView extends GraphView {

    private static final String TAG = "ECGView";
    private ECGSubscriber mSubscriber;
    private Handler mHandler;
    private ECGGraphSeries<DataPoint> mSeries;
    private double graph2LastXValue = 0d;
    private int maxData = 200;


    public ECGView(Context context) {
        super(context);
        init();
    }

    public ECGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ECGView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewport().setYAxisBoundsManual(true);
        getViewport().setMinY(0);
        getViewport().setMaxY(5);

        getViewport().setXAxisBoundsManual(true);
        getViewport().setMinX(0);
        getViewport().setMaxX(maxData);

        mSeries = new ECGGraphSeries<>();
        mSeries.setThickness(5);
        addSeries(mSeries);
    }

    public int getMaxData() {
        return maxData;
    }

    public void setMaxData(int maxData) {
        this.maxData = maxData;
    }

    public ECGSubscriber getSubscriber() {
        return mSubscriber;
    }

    public void setSubscriber(ECGSubscriber subscriber) {
        mSubscriber = subscriber;
        if (subscriber != null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    double ecg = (double) inputMessage.obj;
                    if (graph2LastXValue == maxData) {
                        graph2LastXValue = 0;
                    }
                    else {
                        graph2LastXValue += 1d;
                    }
                    DataPoint d = new DataPoint(graph2LastXValue, ecg);
                    mSeries.appendData(d, false, maxData - 20);
                }
            };
            subscriber.setHandler(mHandler);
        }
    }

}
