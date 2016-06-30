package it.crs4.remotear;

import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;

public class OpticalARToolkit {
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("ARWrapper");
        System.loadLibrary("AROpticalWrapper");
    }
    private ARToolKit arToolKit;

    public float[] getEyeLmodel() {
        return eyeLmodel;
    }

    public float[] getEyeLproject() {
        return eyeLproject;
    }

    public float[] getEyeRmodel() {
        return eyeRmodel;
    }

    public float[] getEyeRproject() {
        return eyeRproject;
    }

    private float [] eyeLmodel = new float [16];
    private float [] eyeLproject = new float [16];

    private float [] eyeRmodel = new float [16];
    private float [] eyeRproject = new float [16];

    public OpticalARToolkit(ARToolKit arToolKit) {
        this.arToolKit = arToolKit;
    }

    public ARToolKit getInstance(){
        return arToolKit;
    }

    public int initialiseAR(String eyeLfilename, String eyeRfilename){
        int ret = getOpticalMatrices(eyeLfilename, eyeRfilename, eyeLmodel, eyeLproject, eyeRmodel, eyeRproject, 1);

        eyeLmodel[12] -=  70;
        eyeRmodel[12] -=  70;

        eyeLmodel[14] -=  100;
        eyeRmodel[14] -=  100;
//        float [] tmp = {
//                5.715693f,
//                0.000000f,
//                0.000000f,
//                0.000000f,
//                0.000000f,
//                10.856619f,
//                0.000000f,
//                0.000000f,
//                0.000000f,
//                0.000000f,
//                -1.002002f,
//                -1.000000f,
//                0.000000f,
//                0.000000f,
//                -20.020020f,
//                0.000000f
//        };
//        eyeRproject = tmp;
//        eyeLproject = tmp;

        return ret;
    }

    private native int getOpticalMatrices(
            String eyeLfilename,
            String eyeRfilename,
            float [] eyeLmodel,
            float [] eyeLproject,
            float [] eyeRmodel,
            float [] eyeRproject,
            float scale
    );

}
