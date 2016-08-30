package it.crs4.most.visualization.augmentedreality;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CameraEventListener;

import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamProperties;
import it.crs4.most.streaming.enums.StreamProperty;
import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.R;
import it.crs4.most.visualization.StreamViewerFragment;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;

//import org.artoolkit.ar.base.ARToolKit;
//import org.artoolkit.ar.base.rendering.Cube;


public class ARFragment extends StreamViewerFragment implements
    CameraEventListener {

    public static final String FRAGMENT_STREAM_ID_KEY = "stream_fragment_stream_id_key";
    private static final String TAG = "ARFragment";
    protected RemoteCaptureCameraPreview preview;
    protected IStream streamAR;
    protected String streamURI;
    protected PubSubARRenderer renderer;
    private IStreamFragmentCommandListener cmdListener = null;
    private SurfaceView surfaceView;
    private View streamCover;
    private TextView txtHiddenSurface;
    private boolean firstUpdate = false;
    private Handler handler;
    private SurfaceHolder.Callback surfaceViewCallback;
    private SurfaceHolder.Callback glSurfaceViewCallback;
    private TouchGLSurfaceView glView;
    private boolean playerButtonsVisible = true;
    private OnCompleteListener mListener;
    private ImageButton butPlay;
    private ImageButton butPause;
    private LinearLayout controlButtonLayout;

    public static ARFragment newInstance(String streamId) {
        ARFragment sf = new ARFragment();

        Bundle args = new Bundle();
        args.putString(FRAGMENT_STREAM_ID_KEY, streamId);
        sf.setArguments(args);

        return sf;
    }

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

    public IStream getStreamAR() {
        return streamAR;
    }

    public void setStreamAR(IStream streamAR) {
        this.streamAR = streamAR;
    }

    public PubSubARRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(PubSubARRenderer renderer) {
        this.renderer = renderer;
    }

    public TouchGLSurfaceView getGlView() {
        return glView;
    }

    private String getStreamId() {
        return getArguments().getString(FRAGMENT_STREAM_ID_KEY);
    }

    @Override
    /**
     * @param activity: the activity attached to this fragment: it must implement the  {@link IStreamFragmentCommandListener} interface
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "ON ATTACH STREAM VIEWER");
        this.cmdListener = (IStreamFragmentCommandListener) activity;
        try {
            this.mListener = (OnCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "ON DETACH STREAM VIEWER");
        this.cmdListener.onSurfaceViewDestroyed(getStreamId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "ON CREATE_VIEW STREAM VIEWER");
        View rootView = inflater.inflate(R.layout.fragment_ar, container, false);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.remoteCameraPreview);
        surfaceView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set

        if (surfaceViewCallback != null) {
            surfaceView.getHolder().addCallback(surfaceViewCallback);
        }

        glView = (TouchGLSurfaceView) rootView.findViewById(R.id.ARSurface);
        glView.getHolder().setFixedSize(704, 576); //FIXME should be dynamically set
        glView.setEGLContextClientVersion(1);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(-3);

        glView.setRenderer(renderer);

        if (glSurfaceViewCallback != null) {
            glView.getHolder().addCallback(glSurfaceViewCallback);
        }

        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        streamCover = rootView.findViewById(R.id.hide_container);
        txtHiddenSurface = (TextView) rootView.findViewById(R.id.txt_hidden_surface);

        butPlay = (ImageButton) rootView.findViewById(R.id.button_play);
        butPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPlay(getStreamId());
            }
        });
        butPlay.setVisibility(playerButtonsVisible ? View.VISIBLE : View.INVISIBLE);

        butPause = (ImageButton) rootView.findViewById(R.id.button_pause);
        butPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ARFragment.this.cmdListener.onPause(getStreamId());
            }
        });
        butPause.setVisibility(playerButtonsVisible ? View.VISIBLE : View.INVISIBLE);
        mListener.onFragmentCreate();

        preview = (RemoteCaptureCameraPreview) rootView.findViewById(R.id.remoteCameraPreview);
        Log.i(TAG, "RemoteCaptureCameraPreview created");
        preview.setCameraListener(this);

        this.controlButtonLayout = (LinearLayout) rootView.findViewById(R.id.control_button_layout);
        this.controlButtonLayout.setVisibility(playerButtonsVisible ? View.VISIBLE : View.INVISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.controlButtonLayout.getLayoutParams();
        if (playerButtonsVisible) {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        else {
            layoutParams.height = 0;
        }
        this.controlButtonLayout.setLayoutParams(layoutParams);

//        setCmdListenerCallback();
        return rootView;
    }

    /**
     * Set the stream visible
     */
    public void setStreamVisible() {
        streamCover.setVisibility(View.INVISIBLE);
        getGlView().setEnabled(true);
    }

    /**
     * Set the stream invisible
     *
     * @param message an optional message to show instead of the stream
     */
    public void setStreamInvisible(String message) {
        this.streamCover.setVisibility(View.VISIBLE);
        this.txtHiddenSurface.setText(message);
        getGlView().setEnabled(false);
    }

    /**
     * Set the player buttons visible or not
     *
     * @param value <code>true</code> set buttons visible; <code>false</code> invisible.
     */
    public void setPlayerButtonsVisible(boolean value) {
        this.playerButtonsVisible = value;
        if (getView() != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.controlButtonLayout.getLayoutParams();
            if (value) {
                this.controlButtonLayout.setVisibility(View.VISIBLE);
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            else {
                this.controlButtonLayout.setVisibility(View.INVISIBLE);
                layoutParams.height = 0;
            }
        }
    }

    public boolean isARRunning() {
        return ARToolKit.getInstance().isRunning();
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
    public void onResume() {
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

    @Override
    public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
        Log.d(TAG, "cameraPreviewStarted!");
        if (!ARToolKit.getInstance().isRunning()) {
            if (ARToolKit.getInstance()
                .initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) {
                firstUpdate = true;
            }
            else {
                Log.e(TAG, "getGLView(): Error initialising camera. Cannot continue.");
            }
        }
    }

    private void prepareAR() {
        ARToolKit.getInstance().initialiseNative(getActivity().getCacheDir().getAbsolutePath());
    }

    public void prepareRemoteAR() {
        prepareAR();

        streamAR.addFrameListener(preview);
//        glView.setRenderer(renderer);
//        glView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glView.setZOrderMediaOverlay(true);
    }

    @Override
    public void cameraPreviewStopped() {
        ARToolKit.getInstance().cleanup();
    }

    @Override
    public void cameraPreviewFrame(byte[] frame) {
        if (this.firstUpdate) {
            if (this.renderer.configureARScene()) {
                Log.i(TAG, "cameraPreviewFrame(): Scene configured successfully");
            }
            else {
                Log.e(TAG, "cameraPreviewFrame(): Error configuring scene. Cannot continue.");
            }

            this.firstUpdate = false;
        }

        if (ARToolKit.getInstance().convertAndDetect(frame)) {
            if (this.glView != null) {
                this.glView.requestRender();
            }

//            this.onFrameProcessed();
        }
        else {
            Log.d(TAG, "no marker found, sorry");
        }

    }

    public static interface OnCompleteListener {
        public abstract void onFragmentCreate();

        public abstract void onFragmentResume();

    }
}
