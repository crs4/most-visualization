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
        return getOpticalMatrices(eyeLfilename, eyeRfilename, eyeLmodel, eyeLproject, eyeRmodel, eyeRproject, 1);
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
