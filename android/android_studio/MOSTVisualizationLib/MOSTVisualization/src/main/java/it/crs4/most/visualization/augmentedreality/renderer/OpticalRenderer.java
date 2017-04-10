package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.OpticalARToolkit;
import it.crs4.most.visualization.augmentedreality.mesh.MeshManager;

public class OpticalRenderer extends PubSubARRenderer {
    private OpticalARToolkit mOpticalARToolkit;
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
        gl.glViewport(0, 0, width/ 2, height);
        float [] model = mOpticalARToolkit.getEyeLmodel();
        basicDraw(gl, mOpticalARToolkit.getEyeLproject(), mOpticalARToolkit.getEyeLmodel());
    }

    private void drawRight(GL10 gl) {
        gl.glViewport(width/ 2, 0, width/ 2, height);
        basicDraw(gl, mOpticalARToolkit.getEyeRproject(), mOpticalARToolkit.getEyeRmodel());
    }

    public EYE getEye() {
        return eye;
    }

    public void setEye(EYE eye) {
        this.eye = eye;
    }
}
