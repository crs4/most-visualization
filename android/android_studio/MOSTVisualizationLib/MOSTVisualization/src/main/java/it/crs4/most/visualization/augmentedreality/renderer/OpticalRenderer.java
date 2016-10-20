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
    }

    private void drawLeft(GL10 gl) {
        gl.glViewport(0, 0, 960 / 2, 436);
        basicDraw(gl, mOpticalARToolkit.getEyeLproject(), mOpticalARToolkit.getEyeLmodel());
    }

    private void drawRight(GL10 gl) {
        gl.glViewport(960 / 2, 0, 960 / 2, 436);
        basicDraw(gl, mOpticalARToolkit.getEyeRproject(), mOpticalARToolkit.getEyeRmodel());
    }
}
