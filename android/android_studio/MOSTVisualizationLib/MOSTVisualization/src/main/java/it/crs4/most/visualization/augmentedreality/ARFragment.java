package it.crs4.most.visualization.augmentedreality;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import org.artoolkit.ar.base.ARToolKit;
//import org.artoolkit.ar.base.rendering.Cube;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.rendering.ARRenderer;

import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamProperties;
import it.crs4.most.streaming.StreamingEventBundle;
import it.crs4.most.streaming.enums.StreamProperty;
import it.crs4.most.streaming.enums.StreamState;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;
import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.R;
import it.crs4.most.visualization.StreamViewerFragment;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;


public class ARFragment extends StreamViewerFragment implements
        CameraEventListener {

    public static final String FRAGMENT_STREAM_ID_KEY = "stream_fragment_stream_id_key";
    private static final String TAG = "StreamViewerFragment";

    private IStreamFragmentCommandListener cmdListener = null;
    private SurfaceView surfaceView;
    private View streamCover;
    private TextView txtHiddenSurface;
    private boolean firstUpdate = false;
    protected RemoteCaptureCameraPreview preview;
    protected IStream streamAR;
    private Handler handler;
    private SurfaceHolder.Callback surfaceViewCallback;
    private SurfaceHolder.Callback glSurfaceViewCallback;

    public SurfaceHolder.Callback getSurfaceViewCallback() {
        return surfaceViewCallback;
    }

    public void setSurfaceViewCallback(SurfaceHolder.Callback surfaceViewCallback) {
        this.surfaceViewCallback = surfaceViewCallback;
    }

    public SurfaceHolder.Callback getGlSurfaceViewCallback() {
        return glSurfaceViewCallback;
    }

    public void setGlSurfaceViewCallback(SurfaceHolder.Callback glSurfaceViewCallback) {
        this.glSurfaceViewCallback = glSurfaceViewCallback;
    }

    public String getStreamURI() {
        return streamURI;
    }

    public void setStreamURI(String streamURI) {
        this.streamURI = streamURI;
    }

    protected String streamURI;

    public IStream getStreamAR() {
        return streamAR;
    }

    public void setStreamAR(IStream streamAR) {
        this.streamAR = streamAR;
    }

    protected PubSubARRenderer renderer;
    public PubSubARRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(PubSubARRenderer renderer) {
        this.renderer = renderer;
    }

    public TouchGLSurfaceView getGlView() {
        return glView;
    }

    private TouchGLSurfaceView glView;

    private boolean playerButtonsVisible = true;

    public static interface OnCompleteListener {
        public abstract void onFragmentCreate();
        public abstract void onFragmentResume();

    }

    private OnCompleteListener mListener;

    public static  ARFragment newInstance(String streamId) {
        ARFragment sf = new ARFragment();

        Bundle args = new Bundle();
        args.putString(FRAGMENT_STREAM_ID_KEY, streamId);
        sf.setArguments(args);

        return sf;
    }

    private String getStreamId()
    {
        return getArguments().getString(FRAGMENT_STREAM_ID_KEY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"ON CREATE STREAM VIEWER");

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


                    } else if (streamState == StreamState.PLAYING) {

                        Log.d(TAG, "event.getData().streamState " + streamState);
                        Log.d(TAG, "ready to call cameraPreviewStarted");

                        //FIXME should be dynamically set
                        int width = 704;
                        int height = 576;
                        Log.d(TAG, "width " + width);
                        Log.d(TAG, "height " + height);
                        cameraPreviewStarted(width, height, 25, 0, false);
                    }
                }
            }
        };
    }

    @Override
    /**
     * @param activity: the activity attached to this fragment: it must implement the  {@link IStreamFragmentCommandListener} interface
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG,"ON ATTACH STREAM VIEWER");
        this.cmdListener = (IStreamFragmentCommandListener) activity;
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d(TAG,"ON DETACH STREAM VIEWER");
        this.cmdListener.onSurfaceViewDestroyed(getStreamId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG,"ON CREATE_VIEW STREAM VIEWER");
        View rootView = inflater.inflate(R.layout.fragment_ar, container, false);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.streamSurface);
        surfaceView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set

        if (surfaceViewCallback != null){
            surfaceView.getHolder().addCallback(surfaceViewCallback);
        }


        glView = (TouchGLSurfaceView) rootView.findViewById(R.id.ARSurface);
        glView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set

        if (glSurfaceViewCallback!= null){
            glView.getHolder().addCallback(glSurfaceViewCallback);
        }

        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        glView.setEGLContextClientVersion(1);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(-3);

        Log.i(TAG, "onResume(): GLSurfaceView created");
//        this.mainLayout.addView(this.preview, new ViewGroup.LayoutParams(-1, -1));

//
        streamCover =  rootView.findViewById(R.id.hidecontainer);
        txtHiddenSurface = (TextView) rootView.findViewById(R.id.txtHiddenSurface);



        ImageButton butPlay = (ImageButton)  rootView.findViewById(R.id.button_play);
        butPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPlay(getStreamId());
            }
        });


        ImageButton butPause = (ImageButton)  rootView.findViewById(R.id.button_pause);
        butPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPause(getStreamId());
            }
        });

        mListener.onFragmentCreate();
//        setCmdListenerCallback();
        return rootView;
    }


    /**
     * Set the stream visible
     */
    public void setStreamVisible()
    {
        streamCover.setVisibility(View.INVISIBLE);
    }


    /**
     * Set the stream invisible
     * @param message an optional message to show instead of the stream
     */
    public void setStreamInvisible(String message)
    {
        this.streamCover.setVisibility(View.VISIBLE);
        this.txtHiddenSurface.setText(message);
    }

    /**
     * Set the player buttons visible or not
     * @param value <code>true</code> set buttons visible; <code>false</code> invisible.
     */
    public void setPlayerButtonsVisible(boolean value)
    {
        this.playerButtonsVisible = value;
        if (getView()==null) return;
        ImageButton butPlay = (ImageButton)  getView().findViewById(R.id.button_play);
        ImageButton butPause = (ImageButton)  getView().findViewById(R.id.button_pause);

        if (value==true) {
            butPlay.setVisibility(View.VISIBLE);
            butPause.setVisibility(View.VISIBLE);
        }
        else {
            butPlay.setVisibility(View.INVISIBLE);
            butPause.setVisibility(View.INVISIBLE);
        }
    }

    private void setProperties() {
        StreamProperties sp = new StreamProperties();
        sp.add(StreamProperty.URI, streamURI);
        if (!streamAR.commitProperties(sp)) {
            Log.e(TAG, "failed setting stream properties");
            throw new RuntimeException("failed setting stream properties");
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        //setCmdListenerCallback();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                ARFragment.this.cmdListener.onSurfaceViewCreated(getStreamId(), surfaceView);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        mListener.onFragmentResume();
    }

    public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
        Log.d(TAG, "cameraPreviewStarted!");
        if (ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) {
//        if (ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para_axis.dat", cameraIndex, cameraIsFrontFacing)) {
            Log.i(TAG, "getGLView(): Camera initialised");
        } else {
            Log.e(TAG, "getGLView(): Error initialising camera. Cannot continue.");
            getActivity().finish();
        }

        Toast.makeText(getActivity(),
                "Camera settings: " + width + "x" + height + "@" + rate + "fps", Toast.LENGTH_SHORT)
                .show();
        firstUpdate = true;
    }

    private void prepareAR() {
        if (!ARToolKit.getInstance().initialiseNative(getActivity().getCacheDir().getAbsolutePath())) {
            (new AlertDialog.Builder(getActivity()))
                    .setMessage("The native library is not loaded. The application cannot continue.")
                    .setTitle("Error")
                    .setCancelable(true)
                    .setNeutralButton(17039360, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    getActivity().finish();
                }
            }).show();
        }
    }

    public void prepareRemoteAR() {
        prepareAR();
        preview = (RemoteCaptureCameraPreview) getActivity().findViewById(R.id.streamSurface);
        Log.i(TAG, "RemoteCaptureCameraPreview created");
        preview.setCameraListener(this);
        streamAR.addFrameListener(preview);
        glView.setRenderer(renderer);
//        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glView.setZOrderMediaOverlay(true);
    }

    @Override
    public void cameraPreviewStopped() {
        ARToolKit.getInstance().cleanup();

    }

    public void cameraPreviewFrame(byte[] frame) {
        if (this.firstUpdate) {
            if (this.renderer.configureARScene()) {
                Log.i(TAG, "cameraPreviewFrame(): Scene configured successfully");
            } else {
                Log.e(TAG, "cameraPreviewFrame(): Error configuring scene. Cannot continue.");
                getActivity().finish();
            }

            this.firstUpdate = false;
        }

        if (ARToolKit.getInstance().convertAndDetect(frame)) {
            if (this.glView != null) {
                this.glView.requestRender();
            }

//            this.onFrameProcessed();
        } else {
            Log.d(TAG, "no marker found, sorry");
        }

    }




}
