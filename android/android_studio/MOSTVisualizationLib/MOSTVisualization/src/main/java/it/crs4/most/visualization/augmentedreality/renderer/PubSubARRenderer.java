package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11;

import it.crs4.most.visualization.augmentedreality.MarkerFactory.Marker;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshFactory;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import io.appium.android.apis.graphics.spritetext.MatrixGrabber;

//import org.artoolkit.ar.base.rendering.Cube;

public class PubSubARRenderer extends ARRenderer implements Handler.Callback {
    //  Code from:  https://groups.google.com/forum/#!topic/android-developers/nSv1Pjp5jLY
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_m = 0;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_out = 36;
    protected volatile float angle = 0;
    protected float previousAngle = 0;
    protected Handler handler;
    protected BaseSubscriber subscriber;
    protected String TAG = "PubSubARRenderer";
    protected int height;
    protected int width;
    protected MeshManager meshManager;
    private boolean enabled = true;
    private int videoHeight;
    private int videoWidth;

    private float viewportAspectRatio = 16f/9f;
    private boolean newViewport = true;
    private HashMap<Mesh, Integer> lastVisibleMarkers = new HashMap<>();
    private static float [] identityM = new float[16];
    private float [] prevModelViewMatrix = new float [16];
    private float lowFilterLevel = 0.9f;

    static {
        Matrix.setIdentityM(identityM, 0);
    }

    public PubSubARRenderer(Context context, MeshManager meshManager) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.meshManager = meshManager;
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

    @Override
    public boolean configureARScene() {
        return meshManager.configureScene();
    }

    /**
     * Should be overridden in subclasses and used to perform rendering.
     */
    public void draw(GL10 gl) {
        float [] projMatrix = ARToolKit.getInstance().getProjectionMatrix();
        updateViewport(gl);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        if (isEnabled()){
            basicDraw(gl, projMatrix);
        }
    }


    protected void basicDraw(GL10 gl, float [] projMatrix){
        basicDraw(gl, projMatrix, identityM);
    }

    protected void basicDraw(GL10 gl, float [] projMatrix, float [] modelMatrix){

        gl.glMatrixMode(GL10.GL_PROJECTION);
        synchronized (meshManager) {
            for (Mesh mesh : meshManager.getMeshes()) {

                List<Marker> markers = mesh.getMarkers();
                if(markers.size() == 0) { //MARKLESS
                    gl.glLoadMatrixf(identityM, 0);
                    mesh.draw(gl);
                }
                else if(projMatrix != null){
                    gl.glLoadMatrixf(projMatrix, 0);
                    gl.glMatrixMode(GL10.GL_MODELVIEW);

                    int visibleMarkerIndex;
                    if (lastVisibleMarkers.containsKey(mesh) && lastVisibleMarkers.get(mesh) > -1){
                        visibleMarkerIndex = lastVisibleMarkers.get(mesh);
                    }
                    else{
                        visibleMarkerIndex = 0;
                        lastVisibleMarkers.put(mesh, visibleMarkerIndex);
                    }

                    if (!ARToolKit.getInstance().
                            queryMarkerVisible(markers.get(visibleMarkerIndex).getArtoolkitID())) {

                        visibleMarkerIndex = -1;
                        for(int i = 0; i < markers.size(); i++){
                            if (i != lastVisibleMarkers.get(mesh) &&
                                    ARToolKit.getInstance().
                                            queryMarkerVisible(markers.get(i).getArtoolkitID())){
                                visibleMarkerIndex = i;
                                break;
                            }
                        }
                    }

                    lastVisibleMarkers.put(mesh, visibleMarkerIndex);

                    if(visibleMarkerIndex >= 0){
                        Marker marker = markers.get(visibleMarkerIndex);

                        gl.glLoadMatrixf(modelMatrix, 0);


                        float[] markerMatrix = ARToolKit.getInstance().
                                queryMarkerTransformation(marker.getArtoolkitID());

                        if(visibleMarkerIndex == lastVisibleMarkers.get(mesh)){
                            for (int i = 0; i < prevModelViewMatrix.length; i++) {
                                prevModelViewMatrix[i] = lowFilterLevel * prevModelViewMatrix[i] + (1 - lowFilterLevel) * markerMatrix[i];
                            }
                        }
                        else{
                            prevModelViewMatrix = markerMatrix;
                        }
                        gl.glMultMatrixf(prevModelViewMatrix, 0);
                        modelMatrix = marker.getModelMatrix();
                        gl.glMultMatrixf(modelMatrix, 0);

                        gl.glPushMatrix();
                        mesh.draw(gl);

//                        MatrixGrabber matrixGrabber = new MatrixGrabber();
//                        matrixGrabber.getCurrentModelView(gl);
//                        float [] currentModelMatrix = matrixGrabber.mModelView;

//                        ((GL11) gl).glGetFloatv(GL11.GL_MODELVIEW, currentModelMatrix, 0);

                        gl.glPopMatrix();
                    }

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
//        getMatrix(gl, GL11.GL_MODELVIEW, modelView);

//        float [] modelView = ARToolKit.getInstance().queryMarkerTransformation(markerID);

        float[] projection = ARToolKit.getInstance().getProjectionMatrix();
//        float [] projection = new float [16];
//        getMatrix(gl, GL11.GL_PROJECTION, projection);;


        int[] view = {0, 0, width, height};
//        gl.glGetIntegerv(GL11. ,viewVectorParams,0);

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
        Log.d(TAG, String.format("onSurfaceChanged width %s, height %s", width, height ));
        this.width = width;
        this.height = height;
        newViewport = true;
        updateViewport(gl);
    }

    private void updateViewport(GL10 gl){
        if (newViewport && width != 0 && height != 0 && videoHeight != 0 && videoWidth!= 0) {

            int finalX, finalY, finalWidth, finalHeight;
            finalX = finalY = finalWidth = finalHeight = 0;
            if(videoWidth >=videoHeight) {

                if (width >= height) {
                    finalX = width/2 - videoWidth*height/(2*videoHeight);
                    finalWidth = videoWidth*height/(videoHeight);
                    finalHeight = height;

                }
                else {
                    finalY = height/2 - videoHeight*width/(2*videoWidth);
                    finalWidth = width;
                    finalHeight = videoHeight*width/videoWidth;
                }
            }

            else { //videoWidth < videoHeight

                if (width <= height) {
                    finalX = width/2 - videoWidth*height/(2*videoHeight);
                    finalWidth = videoWidth*height/(videoHeight);
                    finalHeight = height;
                }
                else {
                    finalY = height/2 - videoHeight*width/(2*videoWidth);
                    finalWidth = width;
                    finalHeight = videoHeight*width/videoWidth;
                }
            }
            Log.d(TAG, String.format(" updateViewport width %s, height %s, videoHeight %s, videoWidth %s",
                    width, height, videoHeight, videoWidth));
            Log.d(TAG, String.format("updateViewport finalX %s, finalY %s, finalWidth %s, finalHeight %s",
                    finalX, finalY, finalWidth, finalHeight));

            gl.glViewport(finalX, finalY, finalWidth, finalHeight);
            newViewport = false;
        }
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        this.draw(gl);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setViewportSize(int width, int height){
        Log.d(TAG, String.format("setViewportSize, width %s, height %s", width, height ));
        videoWidth = width;
        videoHeight = height;
        newViewport = true;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public float getLowFilterLevel() {
        return lowFilterLevel;
    }

    public void setLowFilterLevel(float lowFilterLevel) {
        this.lowFilterLevel = lowFilterLevel;
    }

    public float [] getLastProjMatrix(){
        return ARToolKit.getInstance().getProjectionMatrix();
    }

    public float [] getLastModelMatrix(){
        return prevModelViewMatrix;
    }

//    public float [] getScreenLimit(){
//        float [] projMatrix = getLastProjMatrix();
//        float [] modelMatrix = getLastModelMatrix();
//        float [] finalMatrixToInvert = new float [16];
//        float [] finalMatrix = new float [16];
//        Matrix.multiplyMM(finalMatrixToInvert, 0, projMatrix, 0, modelMatrix, 0);
//        Matrix.invertM(finalMatrix, 0, finalMatrixToInvert, 0);
//
//        float [] topRightCornerNDC = new float[] {1, 1, 1, 1};
//        float [] topRightCorner =  new float[4];
//        Matrix.multiplyMV(topRightCorner, 0, finalMatrix, 0, topRightCornerNDC, 0);
//
//        float [] bottomLeftCornerNDC = new float[] {-1, -1, 1, 1};
//        float [] bottomLeftCorner=  new float[4];
//        Matrix.multiplyMV(bottomLeftCorner, 0, finalMatrix, 0, bottomLeftCornerNDC , 0);
//
//        return new float[] {bottomLeftCorner[0], topRightCorner[0], bottomLeftCorner[1], topRightCorner[1]};
//    }


    public int isMeshVisible(Mesh mesh) {
        float [] identityMatrix = new float [16];
        Matrix.setIdentityM(identityMatrix, 0);
        return isMeshVisible(mesh, identityMatrix);
    }


    /*
    return if mesh is visible after applying modelMatrix over current modelview matrix
     */
    public int isMeshVisible(Mesh mesh, float [] modelMatrix) {
        short[] indices = mesh.getIndices();
        char[] charIndices = new char[indices.length];

        // method needs char[]
        for (int i = 0; i < indices.length; i++) {
            short shortIndex = indices[i];
            charIndices[i] = (char) shortIndex;
        }



        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, getLastModelMatrix(), 0);

        float [] resultMatrix = new float [16];
        Matrix.multiplyMM(resultMatrix, 0, getLastProjMatrix(), 0, modelMatrix, 0);
        return Visibility.visibilityTest(resultMatrix, 0, mesh.getVertices(), 0, charIndices, 0, indices.length);

    }

}