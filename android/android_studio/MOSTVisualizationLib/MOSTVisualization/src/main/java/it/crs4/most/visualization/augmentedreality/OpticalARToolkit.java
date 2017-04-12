package it.crs4.most.visualization.augmentedreality;

import org.artoolkit.ar.base.ARToolKit;

public class OpticalARToolkit {
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("ARWrapper");
        System.loadLibrary("AROpticalWrapper");
    }

    public float[] eyeLmodel = new float[16];
    public float[] eyeLproject = new float[16];
    public float[] eyeRmodel = new float[16];
    public float[] eyeRproject = new float[16];
    private ARToolKit arToolKit;

    public OpticalARToolkit(ARToolKit arToolKit) {
        this.arToolKit = arToolKit;
    }

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

    public ARToolKit getInstance() {
        return arToolKit;
    }

    public int initialiseAR(String eyeLfilename, String eyeRfilename) {
        int ret = getOpticalMatrices(eyeLfilename, eyeRfilename, eyeLmodel, eyeLproject, eyeRmodel, eyeRproject, 1);

//        eyeLmodel[12] -= 70;
//        eyeRmodel[12] -= 70;
//
//        eyeLmodel[14] -= 100;
//        eyeRmodel[14] -= 100;
//        eyeLmodel[12] += 28.000366 -8.274139;
//        eyeLmodel[13] += 5.4715576 + 8.252686;
//        eyeRmodel[12] += -2.8583984;
//        eyeRmodel[13] += 22.968475;
        eyeLmodel[12] = eyeRmodel[12] + 8.8f;
        eyeLmodel[13] = eyeRmodel[13] + 3;

//        eyeRproject[5] += eyeRproject[5]*0.3;
//        eyeLproject[5] = eyeRproject[5];
        return ret;
    }

    private native int getOpticalMatrices(
        String eyeLfilename,
        String eyeRfilename,
        float[] eyeLmodel,
        float[] eyeLproject,
        float[] eyeRmodel,
        float[] eyeRproject,
        float scale
    );

}
