
#include <AR/gsub_es.h>
#include <AR/gsub_mtx.h>
#include <Eden/glm.h>
#include <jni.h>
#include <ARWrapper/ARToolKitWrapperExportedAPI.h>
#include <unistd.h> // chdir()
#include <android/log.h>
#include <AROpticalWrapper.h>


static int setupOptical(const char *optical_param_name, ARdouble m[16], ARdouble p[16], const ARdouble scale)
  {
    ARLOGe("setupOptical");
    ARdouble fovy_p;
    ARdouble aspect_p;
    // Load the optical parameters.
    if (arParamLoadOptical(optical_param_name, &(fovy_p), &(aspect_p), m) < 0) {
    ARLOGe("setupOptical(): Error loading optical parameters from file %s.\n", optical_param_name);
      return (FALSE);
    }
    ARLOGe("*** Optical parameters ***\n");

    arParamDispOptical(fovy_p, aspect_p, m);

    mtxLoadIdentityf(p);
    mtxPerspectivef(p, fovy_p, aspect_p, 10.0, 10000.0);

    return (TRUE);
  }


JNIEXPORT jint JNICALL Java_it_crs4_remotear_OpticalARToolkit_getOpticalMatrices
  (JNIEnv *env,
  jobject thiz,
  jstring eyeLfilename,
  jstring eyeRfilename,
  jfloatArray eyeLmodel,
  jfloatArray eyeLproject,
  jfloatArray eyeRmodel,
  jfloatArray eyeRproject,
  jfloat scale)
{
    ARLOGe("main(): ready to set up optical.\n");
    ARdouble _eyeLmodel[16];
    ARdouble _eyeRmodel[16];
    ARdouble _eyeLproject[16];
    ARdouble _eyeRproject[16];

    const char *nEyeLfilename = env->GetStringUTFChars(eyeLfilename, 0);
    const char *nEyeRfilename = env->GetStringUTFChars(eyeRfilename, 0);

    int res = 1;
    if (!setupOptical("Data/optical_param_left.dat", _eyeLmodel, _eyeLproject, scale) ||
         !setupOptical("Data/optical_param_right.dat", _eyeRmodel, _eyeRproject, scale)) {
        ARLOGe("main(): Unable to set up optical.\n");
        res = 0;
    }
    else{
        env->SetFloatArrayRegion(eyeLmodel, 0, 16, _eyeLmodel);
        env->SetFloatArrayRegion(eyeLproject, 0, 16, _eyeLproject);
        env->SetFloatArrayRegion(eyeRmodel, 0, 16, _eyeRmodel);
        env->SetFloatArrayRegion(eyeRproject, 0, 16, _eyeRproject);
    }

    env->ReleaseStringUTFChars(eyeLfilename, nEyeLfilename);
    env->ReleaseStringUTFChars(eyeRfilename, nEyeRfilename);

    return res;
}

