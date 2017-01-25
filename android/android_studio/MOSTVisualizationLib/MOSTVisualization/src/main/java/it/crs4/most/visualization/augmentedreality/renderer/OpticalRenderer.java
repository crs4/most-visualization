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
    private float [] adjustedCalibration;
    private String TAG = "OpticalRenderer";
    public enum EYE {
        LEFT, RIGHT, BOTH
    };
    private EYE eye = EYE.BOTH;


    public OpticalRenderer(
            Context context,
            OpticalARToolkit opticalARToolkit,
            MeshManager meshManager) {
        super(context, meshManager);
        mOpticalARToolkit = opticalARToolkit;
    }

    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        if (eye == EYE.BOTH || eye == EYE.LEFT ){
            drawLeft(gl);
        }

        if (eye == EYE.BOTH || eye == EYE.RIGHT ){
            drawRight(gl);
        }

    }

    private void drawLeft(GL10 gl) {
        gl.glViewport(0, 0, 960 / 2, 436);
        float [] model = mOpticalARToolkit.getEyeLmodel();
        addAdjustedCalibration(model);
        basicDraw(gl, mOpticalARToolkit.getEyeLproject(), model);
    }

    private void drawRight(GL10 gl) {
        gl.glViewport(960 / 2, 0, 960 / 2, 436);
        float [] model = mOpticalARToolkit.getEyeRmodel();
        addAdjustedCalibration(model);
        basicDraw(gl, mOpticalARToolkit.getEyeRproject(), mOpticalARToolkit.getEyeRmodel());
    }

    public EYE getEye() {
        return eye;
    }

    public void setEye(EYE eye) {
        this.eye = eye;
    }

    public void adjustCalibration(float x, float y , float z) {
        if (adjustedCalibration == null) {
            adjustedCalibration = new float[3];
        }

        adjustedCalibration[0] = x;
        adjustedCalibration[1] = y;
        adjustedCalibration[2] = z;

    }
    private void addAdjustedCalibration(float [] model){
        if (adjustedCalibration != null) {
            model[12] = adjustedCalibration[0];
            model[13] = adjustedCalibration[1];
            model[14] = adjustedCalibration[2];
        }
    }
}
