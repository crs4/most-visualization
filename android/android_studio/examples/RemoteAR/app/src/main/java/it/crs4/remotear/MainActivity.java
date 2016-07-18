package it.crs4.remotear;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;


import android.opengl.GLSurfaceView;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import it.crs4.most.streaming.enums.StreamState;
import it.crs4.most.streaming.enums.StreamingEvent;
import it.crs4.most.streaming.enums.StreamingEventType;
import it.crs4.most.visualization.IStreamFragmentCommandListener;
import it.crs4.most.visualization.StreamInspectorFragment.IStreamProvider;
import it.crs4.most.visualization.augmentedreality.ARFragment;
import it.crs4.most.visualization.augmentedreality.RemoteCaptureCameraPreview;
import it.crs4.most.visualization.augmentedreality.TouchGLSurfaceView;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;
import it.crs4.most.visualization.utils.zmq.ZMQPublisher;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.assets.AssetHelper;
import org.artoolkit.ar.base.camera.CameraEventListener;


public class MainActivity extends BaseRemoteARActivity {

    private static String TAG = "RemoteAR";

    //ID for the menu exit option
    private final int ID_MENU_EXIT = 1;
    private boolean exitFromAppRequest = false;

    private Handler handler;

    //    private StreamViewerFragment stream1Fragment = null;

    private EditText rtspUri;
    private Button playRemoteButton;
    private Button playLocalButton;
    private Button resetButton;

    @Override
    public String supplyStreamURI() {
        // FIXME
        return "rtsp://specialista:speciali@156.148.133.11/mpeg4/media.amp";
    }

    @Override
    public FrameLayout supplyStreamContainer() {
        return (FrameLayout) this.findViewById(R.id.stream_container);
    }

    @Override
    public String supplyStreamName() {
        return "streamAR";
    }

    @Override
    public PubSubARRenderer supplyRenderer() {
        ZMQPublisher publisher = new ZMQPublisher();
        Thread pubThread = new Thread(publisher);
        pubThread.start();
        return new PubSubARRenderer(this, publisher);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playRemoteButton = (Button) findViewById(R.id.play_remote);
        playLocalButton = (Button) findViewById(R.id.play_local);
        resetButton = (Button) findViewById(R.id.reset_button);

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
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetArrowPosition();

            }
        });

        if(savedInstanceState != null && savedInstanceState.getBoolean("mRemotePlay", false)){
            playRemote();
        }

    }

    public void onRadioButtonClicked(View view) {
        if (glView != null) {
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch (view.getId()) {
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

    public void playLocal() {
        Intent intent = new Intent(this, LocalARActivity.class);
        startActivity(intent);
    }

    private void resetArrowPosition(){
        if (glView!= null){
            Mesh arrow = glView.getRenderer().getMesh("arrow");
            arrow.setX(0);
            arrow.setY(0);
            arrow.setZ(0);
        }
    }

}
