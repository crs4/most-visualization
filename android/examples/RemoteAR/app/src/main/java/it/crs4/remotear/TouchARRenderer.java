package it.crs4.remotear;

import android.content.Context;
import android.opengl.Matrix;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.crs4.remotear.mesh.Cube;
import it.crs4.remotear.mesh.Group;
import it.crs4.remotear.mesh.Mesh;
import it.crs4.remotear.mesh.MeshFactory;
import it.crs4.remotear.mesh.Pyramid;
import it.crs4.zmqlib.pubsub.BaseSubscriber;
import it.crs4.zmqlib.pubsub.IPublisher;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TouchARRenderer extends ARRenderer implements Handler.Callback{
    protected  volatile  float angle = 0 ;
    protected  float previousAngle = 0;
    private int markerID = -1;
    private volatile  Group group = new Group("arrow");
    private Pyramid pyramid = new Pyramid(40f, 20f, 40f);
    private Cube cube = new Cube(30f, 20f, 30f);
    private Handler handler;
    protected IPublisher publisher;
    protected BaseSubscriber subscriber;
    protected String TAG = "TouchARRenderer";
    protected int height;
    protected int width;
//    private final ArrayList<Mesh> meshes = new ArrayList<Mesh>();
    private final HashMap<String, Mesh> meshes = new HashMap<String, Mesh>();
    private GL10 gl;

    public TouchARRenderer(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        width = size.x;
//        height = size.y;

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
                        String meshId = json.getString("id");
                        String msgType = (String) json.get("msgType");
                        Mesh mesh;
                        if(msgType.equals("newObj")) {
                            mesh = MeshFactory.createMesh(json);
                            addMesh(mesh);
                        }
                        else{

                            mesh = meshes.get(meshId);
                        }
                        if (mesh != null) {
                            mesh.setCoordinates(
                                    Float.valueOf(json.get("x").toString()),
                                    Float.valueOf(json.get("y").toString()),
                                    Float.valueOf(json.get("z").toString()),
                                    Float.valueOf(json.get("rx").toString()),
                                    Float.valueOf(json.get("ry").toString()),
                                    Float.valueOf(json.get("rz").toString())
                            );
                        }
                        else {
                            Log.e(TAG, "mesh with id " + meshId + " not found!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MeshFactory.MeshCreationFail meshCreationFail) {
                        meshCreationFail.printStackTrace();
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
        pyramid.setRz(180);
//        pyramid.setX(-40f);
        pyramid.setY(-1f*cube.height);
        group.add(cube);
        group.add(pyramid);
        meshes.put(group.getId(), group);

        return  true;
    }

    /**
     * Should be overridden in subclasses and used to perform rendering.
     */
    public void draw(GL10 gl) {
        this.gl = gl;
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

            synchronized (meshes){
                for (Iterator iterator = meshes.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry pair = (Map.Entry)iterator.next();
//                    Mesh mesh = iterator.next();
                    Mesh mesh = (Mesh) pair.getValue();
                    gl.glPushMatrix();
                    mesh.draw(gl);
                    gl.glPopMatrix();
                }
            }
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

    public void addMesh(Mesh mesh){
        synchronized (meshes){
            meshes.put(mesh.getId(), mesh);
        }
    }

    public Mesh getMesh(String id){
        return meshes.get(id);
    }

    public void addMesh(Mesh mesh, float winX, float winY){
        float [] modelView = new float[16];
        Matrix.setIdentityM(modelView, 0);
//        getMatrix(gl, GL10.GL_MODELVIEW, modelView);

//        float [] modelView = ARToolKit.getInstance().queryMarkerTransformation(markerID);

        float [] projection = ARToolKit.getInstance().getProjectionMatrix();
//        float [] projection = new float [16];
//        getMatrix(gl, GL10.GL_PROJECTION, projection);;





        int [] view = {0, 0, width, height};
//        gl.glGetIntegerv(GL10. ,viewVectorParams,0);

        Log.d(TAG, "width " + width + " height " + height);
        float [] newcoords = new float[4];
        winY =  view[3] - winY;

        float winZ = 0;

        GLU.gluUnProject(winX, winY, winZ, modelView, 0, projection, 0, view, 0, newcoords, 0);
//        gluUnProject(winX, winY, winZ, modelView, 0, projection, 0, view, 0, newcoords, 0);

//        Log.d(TAG, "winX " +  winX);
//        Log.d(TAG, "winY " +  winY);

        Log.d(TAG, "newcoords[0]" +  newcoords[0]);
        Log.d(TAG, "newcoords[1]" +  newcoords[1]);
        Log.d(TAG, "newcoords[2]" +  newcoords[2]);
        Log.d(TAG, "newcoords[3]" +  newcoords[3]);

        float x = newcoords[0];
        float y = newcoords[1];
        float z = newcoords[2];
//        float x = newcoords[0] / newcoords[3];
//        float y = newcoords[1] / newcoords[3];
//        float z = newcoords[2] / newcoords[3];
//        float z = 1;

        mesh.setX(x*500);
        mesh.setY(y*500);
        mesh.setZ(z);
//        mesh.setX(0);
//        mesh.setY(0);
//        mesh.setZ(0);

        Log.d(TAG, "adding mesh in x " + x + " y " + y +" z " + z);
        synchronized (meshes){
            addMesh(mesh);
        }

    }

    public Mesh removeMesh(Mesh mesh){
        return meshes.remove(mesh.getId());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height){
        this.width = width;
        this.height = height;
    }



//  Code from:  https://groups.google.com/forum/#!topic/android-developers/nSv1Pjp5jLY
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int     _temp_m   = 0;
    private static final int     _temp_A   = 16;
    private static final int     _temp_in  = 32;
    private static final int     _temp_out = 36;
    public static int gluUnProject(float winx, float winy, float winz,
                                   float model[], int offsetM,
                                   float proj[], int offsetP,
                                   int viewport[], int offsetV,
                                   float[] xyz, int offset)
    {
   /* Transformation matrices */
//   float[] m = new float[16], A = new float[16];
//   float[] in = new float[4], out = new float[4];

   /* Normalize between -1 and 1 */
        _tempGluUnProjectData[_temp_in]   = (winx - viewport[offsetV]) *
                2f / viewport[offsetV+2] - 1.0f;
        _tempGluUnProjectData[_temp_in+1] = (winy - viewport[offsetV+1]) *
                2f / viewport[offsetV+3] - 1.0f;
        _tempGluUnProjectData[_temp_in+2] = 2f * winz - 1.0f;
        _tempGluUnProjectData[_temp_in+3] = 1.0f;

   /* Get the inverse */
        android.opengl.Matrix.multiplyMM(_tempGluUnProjectData, _temp_A,
                proj, offsetP, model, offsetM);
        android.opengl.Matrix.invertM(_tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_A);

        android.opengl.Matrix.multiplyMV(_tempGluUnProjectData, _temp_out,
                _tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_in);
        if (_tempGluUnProjectData[_temp_out+3] == 0.0)
            return GL10.GL_FALSE;

        xyz[offset]  =  _tempGluUnProjectData[_temp_out  ] /
                _tempGluUnProjectData[_temp_out+3];
        xyz[offset+1] = _tempGluUnProjectData[_temp_out+1] /
                _tempGluUnProjectData[_temp_out+3];
        xyz[offset+2] = _tempGluUnProjectData[_temp_out+2] /
                _tempGluUnProjectData[_temp_out+3];
        return GL10.GL_TRUE;
    }



}