package it.crs4.remotear;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;


import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import it.crs4.most.streaming.IStream;
import it.crs4.most.streaming.StreamProperties;
import it.crs4.most.streaming.StreamingEventBundle;
import it.crs4.most.streaming.StreamingLib;
import it.crs4.most.streaming.StreamingLibBackend;
import it.crs4.most.streaming.enums.StreamProperty;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;
import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.StreamInspectorFragment.IStreamProvider;
import it.crs4.zmqlib.pubsub.ZMQPublisher;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.assets.AssetHelper;
import org.artoolkit.ar.base.camera.CameraEventListener;


public class MainActivity extends Activity implements
//        Handler.Callback,
        IStreamFragmentCommandListener,
        IStreamProvider,
        ARFragment.OnCompleteListener,
        CameraEventListener {

    private static String TAG = "RemoteAR";
    private String MAIN_STREAM = "MAIN_STREAM";

    //ID for the menu exit option
    private final int ID_MENU_EXIT = 1;
    private boolean exitFromAppRequest = false;

    private Handler handler;
    private IStream stream1 = null;
    private boolean streaming_ready = false;
    private boolean streamMainDestroyed = false;

//    private StreamViewerFragment stream1Fragment = null;
    private ARFragment stream1Fragment = null;
    private String streamingUri;

    private EditText rtspUri;
    private Button playRemoteButton;
    private Button playLocalButton;
    protected RemoteCaptureCameraPreview preview;
    protected TouchGLSurfaceView glView;
    protected TouchARRenderer renderer;
    protected FrameLayout mainLayout;
    private boolean firstUpdate = false;

    private boolean arFragmentAdded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        File cacheFolder = new File(getCacheDir().getAbsolutePath() + "/Data");

        File[] files = cacheFolder.listFiles();

        for (File file : files) {

            if (!file.delete())
                throw new RuntimeException("cannot delete cached files");
        }

        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(this, "Data");
//        rtspUri = (EditText) findViewById(R.id.rtspUri);
        playRemoteButton = (Button) findViewById(R.id.play_remote);
        playLocalButton = (Button) findViewById(R.id.play_local);

        playRemoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playRemote();
            }
        });
        playLocalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLocal();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        if (glView != null){
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.radio_move:
                    if (checked)
                        glView.setMode(TouchGLSurfaceView.Mode.Move);
                        break;
                case R.id.radio_rotate:
                    if (checked)
                        glView.setMode(TouchGLSurfaceView.Mode.Rotate);
                        break;
                case R.id.radio_edit:
                    if (checked)
                        glView.setMode(TouchGLSurfaceView.Mode.Edit);
                        break;
            }
        }
    }

    public void playLocal(){
        Intent intent = new Intent(this, LocalARActivity.class);
        startActivity(intent);
    }

    public void playRemote(){
        if (!arFragmentAdded){

        this.stream1Fragment = ARFragment.newInstance("stream");
        // add the first fragment to the first container
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(R.id.stream_container, this.stream1Fragment);
        fragmentTransaction.commit();
        Log.d(TAG, "fragmentTransaction.commit");

        this.handler = new Handler(preview);
        this.handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message streamingMessage) {
                StreamingEventBundle event = (StreamingEventBundle) streamingMessage.obj;
                String infoMsg = "Event Type:" + event.getEventType() + " ->" + event.getEvent() + ":" + event.getInfo();
                Log.d(TAG, "handleMessage: Current Event:" + infoMsg);

                if (event.getEventType() == StreamingEventType.STREAM_EVENT &&
                        event.getEvent() == StreamingEvent.VIDEO_SIZE_CHANGED){
                    Log.d(TAG, "ready to call cameraPreviewStarted");
                    IStream stream = MainActivity.this.stream1;
//                    int width = stream.getVideoSize().getWidth();
//                    int height = stream.getVideoSize().getHeight();
                    //FIXME
                    int width = 704;
                    int height = 576;
                    Log.d(TAG, "width " + width);
                    Log.d(TAG, "height " + height);
                    MainActivity.this.cameraPreviewStarted(width, height, 25, 0, false);

                }

            }

        };
        Log.d(TAG, "this.handler set");
        }
        else{
            stream1Fragment.setStreamVisible();
            stream1.play();
        }
    }
    @Override
    public void onSurfaceViewDestroyed(String streamId) {
        if (streamId.equals(MAIN_STREAM))
            this.stream1.destroy();

    }

    @Override
    public void onPause(String streamId) {
        if (streamId.equals(MAIN_STREAM)){
            this.stream1.pause();
        }
    }

    @Override
    public void onPlay(String streamId) {
        if (streamId.equals(MAIN_STREAM)){
            Log.d(TAG, "stream1.play onPlay");
            this.stream1.play();
        }

    }

    @Override
    public List<IStream> getStreams() {
        List<IStream> streams = new ArrayList<IStream>();
        streams.add(this.stream1);
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
    public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView) {
        if (this.stream1 != null)
            this.stream1.prepare(surfaceView);
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
            stream1_params.put("name", MAIN_STREAM);

            this.streamingUri = "rtsp://specialista:speciali@156.148.133.11/mpeg4/media.amp";
            Log.d(TAG, "this.streamingUri: " + this.streamingUri);
            stream1_params.put("uri", this.streamingUri);

            this.stream1 = streamingLib.createStream(stream1_params, this.handler);
            Log.d(TAG, "createStream");

            StreamProperties sp = new StreamProperties();
            sp.add(StreamProperty.URI, this.streamingUri);
            this.stream1.commitProperties(sp);


        } catch (Exception e) {
            streaming_ready = false;
            Log.d(TAG, "ERROR!!!");
            e.printStackTrace();
        }


        streaming_ready = true;
//        this.stream1Fragment.setStreamVisible();
//        this.stream1.play();


    }

    protected TouchARRenderer supplyRenderer() {
        ZMQPublisher publisher = new ZMQPublisher();
        Thread pubThread = new Thread(publisher);
        pubThread.start();
        return new TouchARRenderer(this ,publisher);
    }

    /**
     * Use the FrameLayout in this Activity's UI.
     */

    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.stream_container);
    }



    private void prepareAR(){
        if(!ARToolKit.getInstance().initialiseNative(this.getCacheDir().getAbsolutePath())) {
            (new AlertDialog.Builder(this)).setMessage("The native library is not loaded. The application cannot continue.").setTitle("Error").setCancelable(true).setNeutralButton(17039360, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    MainActivity.this.finish();
                }
            }).show();
        } else {
            this.mainLayout = this.supplyFrameLayout();
            if(this.mainLayout == null) {
                Log.e(TAG, "Error: supplyFrameLayout did not return a layout.");
            } else {
                this.renderer = this.supplyRenderer();
            }
        }
    }

    private void prepareRemoteAR(){

        prepareAR();

        preview = (RemoteCaptureCameraPreview) findViewById(R.id.streamSurface);
        Log.i(TAG, "RemoteCaptureCameraPreview created");
        this.preview.setCameraListener(this);
        this.stream1.addFrameListener(this.preview);

        this.glView = new TouchGLSurfaceView(this);

        ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        this.glView.setEGLContextClientVersion(1);
        this.glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.glView.getHolder().setFormat(-3);
        this.glView.setRenderer(this.renderer);
        this.glView.setRenderMode(0);
        this.glView.setZOrderMediaOverlay(true);
        Log.i(TAG, "onResume(): GLSurfaceView created");
//        this.mainLayout.addView(this.preview, new ViewGroup.LayoutParams(-1, -1));
        this.mainLayout.addView(this.glView, new ViewGroup.LayoutParams(-1, -1));
        Log.i(TAG, "onResume(): Views added to main layout.");

    }


    @Override
    public void onResume() {
        super.onResume();

    if(this.glView != null) {
        this.glView.onResume();
    }
    }


//    protected void onStart() {
//        super.onStart();
//
//    }

    @Override
    public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
        Log.d(TAG, "cameraPreviewStarted!");
        if(ARToolKit.getInstance().initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) {
            Log.i(TAG, "getGLView(): Camera initialised");
        } else {
            Log.e(TAG, "getGLView(): Error initialising camera. Cannot continue.");
            this.finish();
        }

        Toast.makeText(this, "Camera settings: " + width + "x" + height + "@" + rate + "fps", Toast.LENGTH_SHORT).show();
        this.firstUpdate = true;
    }


    public void cameraPreviewFrame(byte[] frame) {
        Log.d(TAG, "cameraPreviewFrame()!");
        if(this.firstUpdate) {
            if(this.renderer.configureARScene()) {
                Log.i(TAG, "cameraPreviewFrame(): Scene configured successfully");
            } else {
                Log.e(TAG, "cameraPreviewFrame(): Error configuring scene. Cannot continue.");
                this.finish();
            }

            this.firstUpdate = false;
        }

        if(ARToolKit.getInstance().convertAndDetect(frame)) {
            Log.d(TAG, "detected marker in frame!");
            if(this.glView != null) {
                Log.d(TAG, "request render on glView");
                this.glView.requestRender();
            }

//            this.onFrameProcessed();
        }
        else{
            Log.d(TAG, "no marker found, sorry");
        }

    }

    @Override
    public void cameraPreviewStopped() {
        ARToolKit.getInstance().cleanup();

    }

    @Override
    public void onFragmentCreate() {
        Log.d(TAG, "onFragmentCreate");
        arFragmentAdded = true;

        setupStreamLib();
        stream1Fragment.setStreamVisible();
        stream1.play();
//        Log.d(TAG, "stream1.play on " + stream1.getProperty(StreamProperty.URI));
    }

    @Override
    public void onFragmentResume() {
        prepareRemoteAR();
    }


}
