package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLU;
import android.opengl.Matrix;
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

import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshFactory;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;
import it.crs4.most.visualization.augmentedreality.mesh.Plane;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import it.crs4.most.visualization.utils.zmq.IPublisher;

//import org.artoolkit.ar.base.rendering.Cube;

public class PubSubARRenderer extends ARRenderer implements Handler.Callback {
    //  Code from:  https://groups.google.com/forum/#!topic/android-developers/nSv1Pjp5jLY
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_m = 0;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_out = 36;
    public IPublisher publisher;
    protected volatile float angle = 0;
    protected float previousAngle = 0;
    protected Handler handler;
    protected BaseSubscriber subscriber;
    protected String TAG = "PubSubARRenderer";
    protected int height;
    protected int width;
    protected MeshManager meshManager;
    private boolean enabled = true;

    public PubSubARRenderer(Context context, MeshManager meshManager) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.meshManager = meshManager;
    }

    public PubSubARRenderer(Context context, IPublisher publisher, MeshManager meshManager) {
        this(context, meshManager);
        setPublisher(publisher);
    }

    public PubSubARRenderer(Context context, BaseSubscriber subscriber, MeshManager meshManager) {
        this(context, meshManager);
        setHandler(subscriber);
    }

    public PubSubARRenderer(Context context, IPublisher publisher, BaseSubscriber subscriber, MeshManager meshManager) {
        this(context, meshManager);
        setPublisher(publisher);
        setHandler(subscriber);
    }

    public MeshManager getMeshManager() {
        return meshManager;
    }

    public void setMeshManager(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    public static int gluUnProject(float winx, float winy, float winz,
                                   float model[], int offsetM,
                                   float proj[], int offsetP,
                                   int viewport[], int offsetV,
                                   float[] xyz, int offset) {
   /* Transformation matrices */
//   float[] m = new float[16], A = new float[16];
//   float[] in = new float[4], out = new float[4];

   /* Normalize between -1 and 1 */
        _tempGluUnProjectData[_temp_in] = (winx - viewport[offsetV]) *
                2f / viewport[offsetV + 2] - 1.0f;
        _tempGluUnProjectData[_temp_in + 1] = (winy - viewport[offsetV + 1]) *
                2f / viewport[offsetV + 3] - 1.0f;
        _tempGluUnProjectData[_temp_in + 2] = 2f * winz - 1.0f;
        _tempGluUnProjectData[_temp_in + 3] = 1.0f;

   /* Get the inverse */
        android.opengl.Matrix.multiplyMM(_tempGluUnProjectData, _temp_A,
                proj, offsetP, model, offsetM);
        android.opengl.Matrix.invertM(_tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_A);

        android.opengl.Matrix.multiplyMV(_tempGluUnProjectData, _temp_out,
                _tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_in);
        if (_tempGluUnProjectData[_temp_out + 3] == 0.0)
            return GL10.GL_FALSE;

        xyz[offset] = _tempGluUnProjectData[_temp_out] /
                _tempGluUnProjectData[_temp_out + 3];
        xyz[offset + 1] = _tempGluUnProjectData[_temp_out + 1] /
                _tempGluUnProjectData[_temp_out + 3];
        xyz[offset + 2] = _tempGluUnProjectData[_temp_out + 2] /
                _tempGluUnProjectData[_temp_out + 3];
        return GL10.GL_TRUE;
    }

    protected void setPublisher(IPublisher publisher) {
        this.publisher = publisher;
    }

    protected void setHandler(BaseSubscriber subscriber) {
        this.subscriber = subscriber;
        if (subscriber != null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    JSONObject json = (JSONObject) inputMessage.obj;

                    try {
                        String meshId = json.getString("id");
                        String msgType = (String) json.get("msgType");
                        Mesh mesh;
                        if (msgType.equals("newObj")) {
                            mesh = MeshFactory.createMesh(json);
                            addMesh(mesh);
                        } else {

                            mesh = meshManager.getMeshByID(meshId);
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
                        } else {
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

    @Override
    public boolean configureARScene() {
        return meshManager.configureScene();
    }

    /**
     * Should be overridden in subclasses and used to perform rendering.
     */
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);



//        Log.d(TAG,"draw mesh size" + meshManager.getMeshes().size());
//        float [] identity = new float[16];
//        Matrix.setIdentityM(identity, 0);
//        gl.glLoadMatrixf(identity, 0);
//
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadMatrixf(identity, 0);
//        Plane p = new Plane(0.1f, 0.1f);
//        p.draw(gl);
//        gl.glMatrixMode(GL10.GL_PROJECTION);



        float [] identityM = new float[16];
        Matrix.setIdentityM(identityM, 0);

        for(Map.Entry<float [], List<Mesh>> entry: meshManager.getVisibleMeshes().entrySet()){

            synchronized (meshManager) {
                for (Mesh mesh : entry.getValue()) {

                    gl.glMatrixMode(GL10.GL_PROJECTION);
                    if (mesh.getMarker() != null){
                        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);
                    }
                    else{
                        gl.glLoadMatrixf(identityM, 0);
                    }

                    gl.glMatrixMode(GL10.GL_MODELVIEW);
                    gl.glLoadMatrixf(entry.getKey(), 0);
                    gl.glPushMatrix();
                    mesh.draw(gl);
                    gl.glPopMatrix();
                }
            }
        }

    }


    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public void addMesh(Mesh mesh) {
        synchronized (meshManager) {
            meshManager.addMesh(mesh);
        }
    }

    public void addMesh(Mesh mesh, float winX, float winY) {
        float[] modelView = new float[16];
        Matrix.setIdentityM(modelView, 0);
//        getMatrix(gl, GL10.GL_MODELVIEW, modelView);

//        float [] modelView = ARToolKit.getInstance().queryMarkerTransformation(markerID);

        float[] projection = ARToolKit.getInstance().getProjectionMatrix();
//        float [] projection = new float [16];
//        getMatrix(gl, GL10.GL_PROJECTION, projection);;


        int[] view = {0, 0, width, height};
//        gl.glGetIntegerv(GL10. ,viewVectorParams,0);

        Log.d(TAG, "width " + width + " height " + height);
        float[] newcoords = new float[4];
        winY = view[3] - winY;

        float winZ = 0;

        GLU.gluUnProject(winX, winY, winZ, modelView, 0, projection, 0, view, 0, newcoords, 0);
//        gluUnProject(winX, winY, winZ, modelView, 0, projection, 0, view, 0, newcoords, 0);

//        Log.d(TAG, "winX " +  winX);
//        Log.d(TAG, "winY " +  winY);

        Log.d(TAG, "newcoords[0]" + newcoords[0]);
        Log.d(TAG, "newcoords[1]" + newcoords[1]);
        Log.d(TAG, "newcoords[2]" + newcoords[2]);
        Log.d(TAG, "newcoords[3]" + newcoords[3]);

        float x = newcoords[0];
        float y = newcoords[1];
        float z = newcoords[2];
//        float x = newcoords[0] / newcoords[3];
//        float y = newcoords[1] / newcoords[3];
//        float z = newcoords[2] / newcoords[3];
//        float z = 1;

        mesh.setX(x * 500);
        mesh.setY(y * 500);
        mesh.setZ(z);
//        mesh.setX(0);
//        mesh.setY(0);
//        mesh.setZ(0);

        Log.d(TAG, "adding mesh in x " + x + " y " + y + " z " + z);
        addMesh(mesh);
    }

//    public Mesh removeMesh(Mesh mesh) {
//        return meshes.remove(mesh.getId());
//    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isEnabled()){
            this.draw(gl);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}