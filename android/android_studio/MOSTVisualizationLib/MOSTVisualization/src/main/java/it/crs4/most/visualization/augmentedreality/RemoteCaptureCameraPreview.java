package it.crs4.most.visualization.augmentedreality;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.artoolkit.ar.base.FPSCounter;
import org.artoolkit.ar.base.camera.CameraEventListener;


import it.crs4.most.streaming.IFrameListener;
import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamingEventBundle;
import it.crs4.most.streaming.enums.StreamState;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;

public class RemoteCaptureCameraPreview extends SurfaceView implements SurfaceHolder.Callback, IFrameListener, Handler.Callback{
    private static final String TAG = "RemoteCameraPreview";
//    private int captureWidth;
//    private int captureHeight;
    private int captureRate;
    private FPSCounter fpsCounter = new FPSCounter();
    private CameraEventListener listener;
    public IStream stream;

    public RemoteCaptureCameraPreview(Context context) {
        super(context);
        Log.d(TAG, "RemoteCameraPreview construct 1");
        init();

//        this.stream = stream;
//        stream.prepare(this);
//        stream.addFrameListener(this);

    }

    private void init(){
        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);

    }
    public RemoteCaptureCameraPreview(Context context, AttributeSet aSet){
        super(context, aSet);
        Log.d(TAG, "RemoteCameraPreview construct 2");
        init();
    }

    public void setCameraListener(CameraEventListener cel){
        listener = cel;
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        if(this.listener != null) {
            this.listener.cameraPreviewStopped();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged");
        if(this.listener != null) {
            Log.d(TAG, "cameraPreviewStarted callback");
//            this.listener.cameraPreviewStarted(stream.getVideoSize().getWidth(),stream.getVideoSize().getHeight(), this.captureRate, 0, false);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void frameReady(byte[] bytes) {
        if(this.listener != null) {
            this.listener.cameraPreviewFrame(bytes);
        }

    }

    @Override
    public boolean handleMessage(Message streamingMessage) {
        Log.d(TAG, "handleMessage");
        // The bundle containing all available informations and resources about the incoming event
        StreamingEventBundle myEvent = (StreamingEventBundle) streamingMessage.obj;

        String infoMsg = "Event Type:" + myEvent.getEventType() + " ->" + myEvent.getEvent() + ":" + myEvent.getInfo();
        Log.d(TAG, "handleMessage: Current Event:" + infoMsg);
        if (myEvent.getEventType() == StreamingEventType.STREAM_EVENT &&
                myEvent.getEvent() == StreamingEvent.VIDEO_SIZE_CHANGED){
            Log.d(TAG, "ready to call cameraPreviewStarted");
            int width = stream.getVideoSize().getWidth();
            int height = stream.getVideoSize().getHeight();
            Log.d(TAG, "width " + width);
            Log.d(TAG, "height " + height);
            this.listener.cameraPreviewStarted(width, height, 25, 0, false);

        }


//        // for simplicity, in this example we only handle events of type STREAM_EVENT
//        if (myEvent.getEventType() == StreamingEventType.STREAM_EVENT)
//            if (myEvent.getEvent() == StreamingEvent.STREAM_STATE_CHANGED || myEvent.getEvent() == StreamingEvent.STREAM_ERROR) {
//
//                // All events of type STREAM_EVENT provide a reference to the stream that triggered it.
//                // In this case we are handling two streams, so we need to check what stream triggered the event.
//                // Note that we are only interested to the new state of the stream
//                IStream stream = (IStream) myEvent.getData();
//                String streamName = stream.getName();
//
//                if (this.stream.getState() == StreamState.DEINITIALIZED && this.exitFromAppRequest) {
//                    if (streamName.equalsIgnoreCase(MAIN_STREAM))
//                        streamMainDestroyed = true;
//
//                    Log.d(TAG, "Stream " + streamName + " deinitialized..");
////                    exitFromApp();
//                }
//            }
//        return false;
    return true;
    }

}
