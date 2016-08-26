package it.crs4.most.visualization.augmentedreality;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.artoolkit.ar.base.assets.AssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamProperties;
import it.crs4.most.streaming.StreamingEventBundle;
import it.crs4.most.streaming.StreamingLib;
import it.crs4.most.streaming.StreamingLibBackend;
import it.crs4.most.streaming.enums.StreamProperty;
import it.crs4.most.streaming.enums.StreamState;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;
import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.StreamInspectorFragment;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;


public abstract class BaseRemoteARActivity extends Activity implements
    IStreamFragmentCommandListener,
    StreamInspectorFragment.IStreamProvider,
    ARFragment.OnCompleteListener {

    protected RemoteCaptureCameraPreview preview;
    protected TouchGLSurfaceView glView;
    protected FrameLayout mainLayout;
    protected ARFragment streamARFragment = null;
    protected PubSubARRenderer renderer;
    String TAG = "BaseRemoteARActivity";
    private IStream streamAR = null;
    private boolean streaming_ready = false;
    private Handler handler;
    private boolean firstUpdate = false;
    private boolean arFragmentAdded = false;
    private boolean mStreamPrepared = false;
    private boolean mRemotePlay = false;
    private FrameLayout streamContainer;

    public abstract String supplyStreamURI();

    public abstract FrameLayout supplyStreamContainer();

    public abstract String supplyStreamName();

    public abstract PubSubARRenderer supplyRenderer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(this, "Data");
        streamARFragment = ARFragment.newInstance(supplyStreamName());
        streamARFragment.setRenderer(supplyRenderer());
    }

    private void setProperties() {
        StreamProperties sp = new StreamProperties();
        sp.add(StreamProperty.URI, supplyStreamURI());
        if (!streamAR.commitProperties(sp)) {
            Log.e(TAG, "failed setting stream properties");
            throw new RuntimeException("failed setting stream properties");
        }
    }

    private void setupStreamLib() {
        Log.d(TAG, "setupStreamLib");
        try {

            StreamingLib streamingLib = new StreamingLibBackend();
            Log.d(TAG, "streamingLib created");
            // First of all, initialize the library
            streamingLib.initLib(this.getApplicationContext());
            Log.d(TAG, "streamingLib initLib");
            // Instance the first stream

            HashMap<String, String> stream1_params = new HashMap<String, String>();
            stream1_params.put("name", supplyStreamName());

            Log.d(TAG, "this.streamingUri: " + supplyStreamURI());
            stream1_params.put("uri", supplyStreamURI());

            this.streamAR = streamingLib.createStream(stream1_params, this.handler);
            Log.d(TAG, "createStream");


        }
        catch (Exception e) {
            streaming_ready = false;
            Log.d(TAG, "ERROR!!!");
            e.printStackTrace();
        }
        streaming_ready = true;
    }

    public void playRemote() {
        mRemotePlay = true;
        if (!arFragmentAdded) {
            // add the first fragment to the first container
            FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
            fragmentTransaction.add(supplyStreamContainer().getId(), streamARFragment);
            fragmentTransaction.commit();
            Log.d(TAG, "fragmentTransaction.commit");

            handler = new Handler(preview);
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message streamingMessage) {
                    StreamingEventBundle event = (StreamingEventBundle) streamingMessage.obj;
                    String infoMsg = "Event Type:" + event.getEventType() + " ->" + event.getEvent() + ":" + event.getInfo();
                    Log.d(TAG, "handleMessage: Current Event:" + infoMsg);

                    StreamState streamState = ((IStream) event.getData()).getState();
                    Log.d(TAG, "event.getData().streamState " + streamState);
                    if (event.getEventType() == StreamingEventType.STREAM_EVENT &&
                        event.getEvent() == StreamingEvent.STREAM_STATE_CHANGED

                        ) {
                        if (streamState == StreamState.INITIALIZED) {
                            setProperties();
                            streamAR.play();


                        }
                        else if (streamState == StreamState.PLAYING) {

                            Log.d(TAG, "event.getData().streamState " + streamState);
                            Log.d(TAG, "ready to call cameraPreviewStarted");
                            IStream stream = BaseRemoteARActivity.this.streamAR;
                            //FIXME should be dynamically set
                            int width = 704;
                            int height = 576;
                            Log.d(TAG, "width " + width);
                            Log.d(TAG, "height " + height);
                            streamARFragment.cameraPreviewStarted(width, height, 25, 0, false);
                        }
                    }
                }
            };
            Log.d(TAG, "this.handler set");
        }
        else {
            streamARFragment.setStreamVisible();
        }
    }


    @Override
    public void onSurfaceViewDestroyed(String streamId) {
        this.streamAR.destroy();
    }

    @Override
    public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView) {
        Log.d(TAG, "onSurfaceViewCreated!!!");
        if (surfaceView != null && !mStreamPrepared) {
            setupStreamLib();
            streamAR.prepare(surfaceView, true);
            streamARFragment.setStreamAR(streamAR);
//            streamARFragment.setRenderer(supplyRenderer());
            streamARFragment.prepareRemoteAR();
            mStreamPrepared = true;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (this.glView != null) {
//            this.glView.onResume();
//        }
//    }

    @Override
    public void onFragmentCreate() {
        Log.d(TAG, "onFragmentCreate");
        arFragmentAdded = true;
    }

    @Override
    public void onFragmentResume() {
    }

    @Override
    public List<IStream> getStreams() {
        List<IStream> streams = new ArrayList<IStream>();
        streams.add(this.streamAR);
        return streams;
    }

    @Override
    public List<StreamProperty> getStreamProperties() {
        ArrayList<StreamProperty> streamProps = new ArrayList<StreamProperty>();
        streamProps.add(StreamProperty.NAME);
        streamProps.add(StreamProperty.STATE);
        return streamProps;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mRemotePlay", mRemotePlay);
    }

    @Override
    public void onPause(String streamId) {
        this.streamAR.pause();
    }

    @Override
    public void onPlay(String streamId) {
        Log.d(TAG, "stream1.play onPlay");
        this.streamAR.play();
    }

}
