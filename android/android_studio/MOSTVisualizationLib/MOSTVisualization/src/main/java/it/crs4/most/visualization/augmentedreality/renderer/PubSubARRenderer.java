package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.MarkerFactory.Marker;
import it.crs4.most.visualization.augmentedreality.mesh.Line;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;

public class PubSubARRenderer extends ARRenderer implements Handler.Callback {
    //  Code from:  https://groups.google.com/forum/#!topic/android-developers/nSv1Pjp5jLY
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_m = 0;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_out = 36;
    protected volatile float angle = 0;
    protected String TAG = "PubSubARRenderer";
    protected int height;
    protected int width;
    protected MeshManager meshManager;
    private boolean enabled = true;
    private int videoHeight;
    private int videoWidth;
    protected float [] extraCalibration = new float[3];
    private boolean newViewport = true;
    private HashMap<Mesh, Integer> lastVisibleMarkers = new HashMap<>();
    private static float [] identityM = new float[16];
    private Map<Mesh, float []> prevModelViewMatrixMap = new HashMap<>();
    private float lowFilterLevel = 0.9f;
    private boolean drawInvisibilityLine = true;
    private boolean adaptViewportToVideo = true;


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

                        float[] markerMatrix = ARToolKit.getInstance().
                                queryMarkerTransformation(marker.getArtoolkitID());

                        float [] prevModelViewMatrix;
                        if(visibleMarkerIndex == lastVisibleMarkers.get(mesh)){

                            if (!prevModelViewMatrixMap.containsKey(mesh)) {
                                prevModelViewMatrixMap.put(mesh, new float [16]);
                            }
                            prevModelViewMatrix = prevModelViewMatrixMap.get(mesh);
                            for (int i = 0; i < prevModelViewMatrix.length; i++) {
                                prevModelViewMatrix[i] = lowFilterLevel * prevModelViewMatrix[i] + (1 - lowFilterLevel) * markerMatrix[i];
                            }
                        }
                        else{
                            prevModelViewMatrix = markerMatrix;
                        }
                        prevModelViewMatrixMap.put(mesh, prevModelViewMatrix);

                        float [] finalModelMatrix = multiplyMatrix(
                                getExtraCalibrationMatrix(),
                                multiplyMatrix(
                                        marker.getModelMatrix(),
                                        multiplyMatrix(modelMatrix, prevModelViewMatrix)
                                )
                        );
                        gl.glLoadMatrixf(finalModelMatrix, 0);
                        gl.glPushMatrix();
                        mesh.draw(gl);
                        gl.glPopMatrix();

                        float [] finalMatrix = multiplyMatrix(
                                projMatrix,
                                multiplyMatrix(
                                        mesh.getTransMatrix(),
                                        finalModelMatrix
                                        )
                        );

                        if (isDrawInvisibilityLine() && isMeshVisible(mesh,finalMatrix) < 1) {
                            Line line = new Line(
                                    new float[3],
                                    new float[] {mesh.getX(), mesh.getY(), mesh.getZ()},
                                    1f
                            );
                            line.setColors(new float[]{1.0F, 1F, 1F, 1.0F});
                            line.draw(gl);
                        }

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
        float[] projection = ARToolKit.getInstance().getProjectionMatrix();
        int[] view = {0, 0, width, height};

        Log.d(TAG, "width " + width + " height " + height);
        float[] newcoords = new float[4];
        winY = view[3] - winY;

        float winZ = 0;

        GLU.gluUnProject(winX, winY, winZ, modelView, 0, projection, 0, view, 0, newcoords, 0);

        Log.d(TAG, "newcoords[0]" + newcoords[0]);
        Log.d(TAG, "newcoords[1]" + newcoords[1]);
        Log.d(TAG, "newcoords[2]" + newcoords[2]);
        Log.d(TAG, "newcoords[3]" + newcoords[3]);

        float x = newcoords[0];
        float y = newcoords[1];
        float z = newcoords[2];
        mesh.setX(x * 500);
        mesh.setY(y * 500);
        mesh.setZ(z);
        Log.d(TAG, "adding mesh in x " + x + " y " + y + " z " + z);
        addMesh(mesh);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, String.format("onSurfaceChanged width %s, height %s", width, height ));
        this.width = width;
        this.height = height;
        newViewport = true;
        updateViewport(gl);
    }

    private void updateViewport(GL10 gl){

        if (newViewport &&  width != 0 && height != 0 && videoHeight != 0 && videoWidth!= 0) {
            int finalX, finalY, finalWidth, finalHeight;
            finalX = finalY = finalWidth = finalHeight = 0;

            if (!adaptViewportToVideo) {
                finalWidth = width;
                finalHeight = height;
            }

            else {
//              Lets try if height fits
                int tmpWidth = height*videoWidth/videoHeight;
                if (tmpWidth < width) {
                    finalWidth = tmpWidth;
                    finalX = width/2 - finalWidth/2;
                    finalY = 0;
                    finalHeight = height;
                }
                else {
                    finalWidth = width;
                    finalX = 0;
                    finalHeight = finalWidth*videoHeight/videoWidth;
                    finalY = height/2 - finalHeight/2;
                }

//                if (videoWidth >= videoHeight) {
//
//                    if (width >= height) {
//                        if (width >= videoWidth) {
//                            finalHeight = height;
//                            finalWidth = finalHeight*videoWidth/videoHeight;
//                            if (finalWidth > width) {
//                                finalWidth = width;
//                                finalHeight = finalWidth*height/width;
//                            }
//                            finalX = width / 2 - finalWidth/2;
//                        }
//                        else {
//                            finalX = 0;
//                            finalWidth = width;
//                            finalHeight = videoHeight/videoWidth*finalWidth;
//                        }
//
//                    } else {
//                        finalY = height / 2 - videoHeight * width / (2 * videoWidth);
//                        finalWidth = width;
//                        finalHeight = videoHeight * width / videoWidth;
//                    }
//                } else { //videoWidth < videoHeight
//
//                    if (width <= height) {
//                        finalX = width / 2 - videoWidth * height / (2 * videoHeight);
//                        finalWidth = videoWidth * height / (videoHeight);
//                        finalHeight = height;
//                    } else {
//                        finalY = height / 2 - videoHeight * width / (2 * videoWidth);
//                        finalWidth = width;
//                        finalHeight = videoHeight * width / videoWidth;
//                    }
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

//    public float [] getLastProjMatrix(){
//        return ARToolKit.getInstance().getProjectionMatrix();
//    }
//
//    public float [] getLastModelMatrix(){
//        return prevModelViewMatrix;
//    }
//
    public float [] getExtraCalibrationMatrix() {
        float [] extraCalibrationMatrix = new float [16];
        Matrix.setIdentityM(extraCalibrationMatrix, 0);
        extraCalibrationMatrix[12] = extraCalibration[0];
        extraCalibrationMatrix[13] = extraCalibration[1];
        extraCalibrationMatrix[14] = extraCalibration[2];
        return extraCalibrationMatrix;
    }

    /*
    return if mesh is visible after applying modelMatrix over current modelview matrix
     */
    public int isMeshVisible(Mesh mesh, float [] matrix) {
        short[] indices = mesh.getIndices();
        char[] charIndices = new char[indices.length];

        // method needs char[]
        for (int i = 0; i < indices.length; i++) {
            short shortIndex = indices[i];
            charIndices[i] = (char) shortIndex;
        }
        return Visibility.visibilityTest(matrix, 0, mesh.getVertices(), 0, charIndices, 0, indices.length);

    }


    public float[] getExtraCalibration() {
        return extraCalibration;
    }

    public void setExtraCalibration(float[] extraCalibration) {
        this.extraCalibration = extraCalibration;
    }

    private float [] multiplyMatrix(float [] matrixL, float [] matrixR) {
        float [] result = new float [16];
        Matrix.multiplyMM(result, 0, matrixL, 0, matrixR, 0);
        return result;
    }

    public boolean isDrawInvisibilityLine() {
        return drawInvisibilityLine;
    }

    public void setDrawInvisibilityLine(boolean drawInvisibilityLine) {
        this.drawInvisibilityLine = drawInvisibilityLine;
    }

    public boolean isAdaptViewportToVideo() {
        return adaptViewportToVideo;
    }

    public void setAdaptViewportToVideo(boolean adaptViewportToVideo) {
        this.adaptViewportToVideo = adaptViewportToVideo;
    }
}