package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;

import org.artoolkit.ar.base.ARToolKit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.OpticalARToolkit;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import it.crs4.most.visualization.utils.zmq.IPublisher;

public class OpticalRenderer extends PubSubARRenderer {
    private OpticalARToolkit mOpticalARToolkit;
    private String TAG = "OpticalRenderer";


    public OpticalRenderer(
            Context context,
            OpticalARToolkit opticalARToolkit,
            MeshManager meshManager) {
        super(context, meshManager);
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
        for(Map.Entry<float [], List<Mesh>> entry: meshManager.getVisibleMeshes().entrySet()){
            gl.glPushMatrix();
            gl.glMultMatrixf(entry.getKey(), 0);

            synchronized (meshManager) {
                for (Mesh mesh : entry.getValue()) {
                    gl.glPushMatrix();
                    mesh.draw(gl);
                    gl.glPopMatrix();
                }
            }
            gl.glPopMatrix();
        }
    }
}
