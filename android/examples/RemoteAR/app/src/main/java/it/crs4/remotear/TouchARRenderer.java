package it.crs4.remotear;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES10;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.json.JSONException;
import org.json.JSONObject;
//import org.artoolkit.ar.base.rendering.Cube;
import it.crs4.remotear.mesh.Cube;
import it.crs4.remotear.mesh.Group;
import it.crs4.remotear.mesh.Pyramid;
import it.crs4.zmqlib.pubsub.BaseSubscriber;
import it.crs4.zmqlib.pubsub.IPublisher;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TouchARRenderer extends ARRenderer implements Handler.Callback{
    protected  volatile  float angle = 0 ;
    protected  float previousAngle = 0;
    private int markerID = -1;
    private volatile  Group group = new Group();
    private Pyramid pyramid = new Pyramid(40f, 80f, 40f);
    private Cube cube = new Cube(60f, 20f, 20f);
    private Handler handler;
    protected IPublisher publisher;
    protected BaseSubscriber subscriber;
    protected String TAG = "TouchARRenderer";
    protected int height;
    protected int width;


    public TouchARRenderer(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

    }

    public TouchARRenderer(Context context, IPublisher publisher){
        this(context);
        setPublisher(publisher);
    }

    public TouchARRenderer(Context context, BaseSubscriber subscriber){
        this(context);
        setHandler(subscriber);
    }

    protected void setPublisher(IPublisher publisher){
        this.publisher = publisher;
        if (publisher != null){
            group.publisher = publisher;
        }
    }

    protected void setHandler(BaseSubscriber subscriber){
        this.subscriber = subscriber;
        if (subscriber != null){
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    JSONObject json = (JSONObject) inputMessage.obj;
                    try {
                        group.setCoordinates(
                                Float.valueOf(json.get("x").toString()),
                                Float.valueOf(json.get("y").toString()),
                                Float.valueOf(json.get("z").toString()),
                                Float.valueOf(json.get("rx").toString()),
                                Float.valueOf(json.get("ry").toString()),
                                Float.valueOf(json.get("rz").toString())
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            subscriber.handler = handler;
        }
    }

    public TouchARRenderer(IPublisher publisher, BaseSubscriber subscriber){
        setPublisher(publisher);
        setHandler(subscriber);
    }

    @Override
    public boolean configureARScene() {

        markerID = ARToolKit.getInstance().addMarker("single;Data/hiro.patt;80");
        if (markerID < 0) return false;

//        cube.setX(-20f);
        pyramid.setRz(90);
//        pyramid.setX(-40f);
//        pyramid.rx = 90;

        group.add(cube);
        group.add(pyramid);

        return  true;
    }

    /**
     * Should be overridden in subclasses and used to perform rendering.
     */
    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
////
////                 Apply the ARToolKit projection matrix

        gl.glMatrixMode(GL10.GL_PROJECTION);
        float [] projectMatrix = ARToolKit.getInstance().getProjectionMatrix();
        gl.glLoadMatrixf(projectMatrix, 0);

        // If the marker is visible, apply its transformation, and draw a pyramid
        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadMatrixf(ARToolKit.getInstance().queryMarkerTransformation(markerID), 0);

            if (angle != previousAngle){
                group.setRy(angle);
            }
            previousAngle = angle;

            group.draw(gl);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        // Sets the current view port to the new size.
//        gl.glViewport(0, 0, width, height);
//        // Select the projection matrix
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        // Reset the projection matrix
//        gl.glLoadIdentity();
//        // Calculate the aspect ratio of the window
//        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
//                100.0f);
//        // Select the modelview matrix
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        // Reset the modelview matrix
//        gl.glLoadIdentity();
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        // Set the background color to black ( rgba ).
//        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
//        // Enable Smooth Shading, default not really needed.
//        gl.glShadeModel(GL10.GL_SMOOTH);
//        // Depth buffer setup.
//        gl.glClearDepthf(1.0f);
//        // Enables depth testing.
//        gl.glEnable(GL10.GL_DEPTH_TEST);
//        // The type of depth testing to do.
//        gl.glDepthFunc(GL10.GL_LEQUAL);
//        // Really nice perspective calculations.
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//    }



}