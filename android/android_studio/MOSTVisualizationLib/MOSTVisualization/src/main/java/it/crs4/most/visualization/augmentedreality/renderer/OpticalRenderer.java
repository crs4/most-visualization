package it.crs4.most.visualization.augmentedreality.renderer;

import android.content.Context;
import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;

import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import it.crs4.most.visualization.augmentedreality.OpticalARToolkit;
import it.crs4.most.visualization.augmentedreality.mesh.Mesh;
import it.crs4.most.visualization.utils.zmq.BaseSubscriber;
import it.crs4.most.visualization.utils.zmq.IPublisher;

public class OpticalRenderer extends PubSubARRenderer{
    private OpticalARToolkit mOpticalARToolkit;
    private String TAG = "OpticalRenderer";


    public OpticalRenderer (Context context, OpticalARToolkit opticalARToolkit){
        super(context);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(Context context, IPublisher publisher, OpticalARToolkit opticalARToolkit){
        super(context, publisher);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(
            Context context,
            BaseSubscriber subscriber,
            OpticalARToolkit opticalARToolkit){
        super(context, subscriber);
        mOpticalARToolkit = opticalARToolkit;
    }

    public OpticalRenderer(
            Context context,
            IPublisher publisher,
            BaseSubscriber subscriber,
            OpticalARToolkit opticalARToolkit) {

        super(context, publisher, subscriber);
        mOpticalARToolkit = opticalARToolkit;
    }

    public void draw(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        drawLeft(gl);
        drawRight(gl);
//        super.draw(gl);

    }

    protected void drawLeft(GL10 gl){
        gl.glViewport(0, 0, 960/2, 436);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        float [] projectMatrix = ARToolKit.getInstance().getProjectionMatrix();
//        gl.glLoadMatrixf(projectMatrix, 0);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeLproject(), 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeLmodel(), 0);
        basicDraw(gl);

    }
    protected void drawRight(GL10 gl){
        gl.glViewport(960/2, 0 , 960/2, 436);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        float [] projectMatrix = ARToolKit.getInstance().getProjectionMatrix();
//        gl.glLoadMatrixf(projectMatrix, 0);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeRproject(), 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(mOpticalARToolkit.getEyeRmodel(), 0);
        basicDraw(gl);

    }
    protected void basicDraw(GL10 gl){


        if (ARToolKit.getInstance().queryMarkerVisible(markerID)) {
            float [] trans = ARToolKit.getInstance().queryMarkerTransformation(markerID);
            gl.glMultMatrixf(trans, 0);
//            gl.glLoadMatrixf(trans, 0);

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


}
