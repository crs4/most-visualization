package it.crs4.most.visualization.augmentedreality;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.artoolkit.ar.base.FPSCounter;
import org.artoolkit.ar.base.camera.CameraEventListener;

import it.crs4.most.streaming.IEventListener;
import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamingEventBundle;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;

public class RemoteCaptureCameraPreview extends SurfaceView implements SurfaceHolder.Callback, IEventListener{
    private static final String TAG = "RemoteCameraPreview";
    public IStream stream;
    //    private int captureWidth;
//    private int captureHeight;
    private int captureRate;
    private FPSCounter fpsCounter = new FPSCounter();
    private CameraEventListener listener;
    private int videoWidth;
    private int videoHeight;

    public RemoteCaptureCameraPreview(Context context) {
        super(context);
        Log.d(TAG, "RemoteCameraPreview construct 1");
        init();

    }

    public RemoteCaptureCameraPreview(Context context, AttributeSet aSet) {
        super(context, aSet);
        Log.d(TAG, "RemoteCameraPreview construct 2");
        init();
    }

    private void init() {
        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);

    }

    public void setCameraListener(CameraEventListener cel) {
        listener = cel;
    }

    public void setStream(IStream stream) {
        this.stream = stream;
        this.stream.addEventListener(this);
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.listener != null) {
            this.listener.cameraPreviewStopped();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }

    @Override
    public void frameReady(byte[] bytes) {
        if (this.listener != null) {
            float expectedBufferLenght = videoHeight*videoWidth*1.5f;
            if (bytes.length == expectedBufferLenght)
                this.listener.cameraPreviewFrame(bytes);
            else
                Log.d(TAG, String.format("frame size %s != %s, dropping", bytes.length, expectedBufferLenght));
        }

    }

    @Override
    public void onPlay() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onVideoChanged(int width, int height) {
        videoWidth = width;
        videoHeight = height;
        this.listener.cameraPreviewStarted(width, height, 25, 0, false);
    }

    public IStream getStream() {
        return stream;
    }
}
