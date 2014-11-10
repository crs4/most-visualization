
Examples
========

Android
-------

 * **StillImageExample** located into the folder *examples/android*. This example explains you:
  - how to play a stream on a StreamViewerFragment 
  - how to get real-time informations about the stream by using a StreamInspector fragment
  - how to change the Stream Rendering mode. You can choose among the following 3 modalities:
    - rstp streamning (the continous stream that the user can play or pause by clicking the playerr buttons provided by the StreamViewer fragment)
    - motion-jpeg (the system loads a jpeg image from the remote camera every # seconds, as specified by the user)
    - still-image (you load a snapshot from the renmote camera by clicking on a button)
 
  For running the Android example, open your preferred IDE (e.g Eclipse) and do the following changes:
   - Import the Most-Streaming project library 
   - Edit the file *jni/Android.mk* and properly change the absolute path of the environment variables GSTREAMER_SDK_ROOT_ANDROID and GSTREAMER_ROOT 
   - Import the StillImage Android project example located in to the *android/examples* folder and add thr Most-Streaming project as a project reference
   - Create your *uri.properties.default* property file and put it into the *assets* folder.(That folder already contains the *uri.properties* file that you can use as template for your own property file)
   - Build the projects (Note that the NDK must be installed and configurated on your system in order to build the project)
   - Deploy the application on your android device or on your emulator 
