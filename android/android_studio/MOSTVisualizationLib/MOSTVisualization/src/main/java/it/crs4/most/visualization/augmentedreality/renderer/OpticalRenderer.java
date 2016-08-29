package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;

import org.artoolkit.ar.base.ARToolKit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.OpticalARToolkit;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import it.crs4.most.visualization.utils.zmq.IPublisher;

public class OpticalRenderer extends PubSubARRenderer {
    private OpticalARToolkit mOpticalARToolkit;
    private String TAG = "OpticalRenderer";


    public OpticalRenderer(
            Context context,
            OpticalARToolkit opticalARToolkit,
            HashMap<String, Mesh> meshes) {
        super(context, meshes);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(
            Context context,
            IPublisher publisher,
            OpticalARToolkit opticalARToolkit,
            HashMap<String, Mesh> meshes) {
        super(context, publisher, meshes);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(
            Context context,
            BaseSubscriber subscriber,
            OpticalARToolkit opticalARToolkit,
            HashMap<String, Mesh> meshes) {

        super(context, subscriber, meshes);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(
            Context context,
            IPublisher publisher,
            BaseSubscriber subscriber,
            OpticalARToolkit opticalARToolkit,
            HashMap<String, Mesh> meshes) {

        super(context, publisher, subscriber, meshes);
        mOpticalARToolkit = opticalARToolkit;
    }

    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        drawLeft(gl);
        drawRight(gl);
//        super.draw(gl);

    }

    protected void drawLeft(GL10 gl) {
        gl.glViewport(0, 0, 960 / 2, 436);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        float[] projectMatrix = ARToolKit.getInstance().getProjectionMatrix();
//        gl.glLoadMatrixf(projectMatrix, 0);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeLproject(), 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeLmodel(), 0);
        basicDraw(gl);

    }

    protected void drawRight(GL10 gl) {
        gl.glViewport(960 / 2, 0, 960 / 2, 436);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        float[] projectMatrix = ARToolKit.getInstance().getProjectionMatrix();
//        gl.glLoadMatrixf(projectMatrix, 0);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeRproject(), 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeRmodel(), 0);
        basicDraw(gl);

    }

    protected void basicDraw(GL10 gl) {
        for (int markerID : markersID.keySet()) {
            if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
                float[] trans = ARToolKit.getInstance().queryMarkerTransformation(markerID);
                gl.glPushMatrix();
                gl.glMultMatrixf(trans, 0);

                synchronized (markerToMeshes) {
                    for (Mesh mesh : markerToMeshes.get(markerID)) {
                        gl.glPushMatrix();
                        mesh.draw(gl);
                        gl.glPopMatrix();
                    }
                }
                gl.glPopMatrix();
            }
        }
    }
}
