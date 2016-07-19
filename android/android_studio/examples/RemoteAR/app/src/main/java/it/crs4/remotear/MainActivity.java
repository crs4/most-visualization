package it.crs4.remotear;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.EditText;
import it.crs4.most.visualization.augmentedreality.TouchGLSurfaceView;
import it.crs4.most.visualization.augmentedreality.mesh.Cube;
import it.crs4.most.visualization.augmentedreality.mesh.Group;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.Pyramid;
import it.crs4.most.visualization.augmentedreality.renderer.PubSubARRenderer;
import it.crs4.most.visualization.utils.zmq.ZMQPublisher;
import it.crs4.most.visualization.augmentedreality.BaseRemoteARActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import java.util.HashMap;

public class MainActivity extends BaseRemoteARActivity implements SurfaceHolder.Callback {

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
    protected HashMap<String, Mesh> meshes = new HashMap<>();

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
        return renderer;
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

        ZMQPublisher publisher = new ZMQPublisher();
        Thread pubThread = new Thread(publisher);
        pubThread.start();

        Group group = new Group("arrow");
        Pyramid pyramid = new Pyramid(40f, 20f, 40f);
        Cube cube = new Cube(30f, 20f, 30f);
        group.publisher = publisher;
        pyramid.setRz(180);
//        pyramid.setX(-40f);
        pyramid.setY(-1f*cube.height);
        group.add(cube);
        group.add(pyramid);
        meshes.put(group.getId(), group);
        renderer =  new PubSubARRenderer(this, publisher);
        renderer.setMeshes(meshes);
        streamARFragment.setGlSurfaceViewCallback(this);
        streamARFragment.setSurfaceViewCallback(this);


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
            Mesh arrow = meshes.get("arrow");
            arrow.setX(0);
            arrow.setY(0);
            arrow.setZ(0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        streamARFragment.getGlView().setMeshes(meshes);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
