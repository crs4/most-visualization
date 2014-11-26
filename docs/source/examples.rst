
Examples
========

Android
-------

All the following examples are located into the folder *examples/android*. 

 * **StreamInspectorExample** This example explains you:
  - how to play a stream on a *StreamViewerFragment* 
  - how to get and/or update the properties of the stream by using a *StreamInspectorFragment*
  - how to change the Stream Rendering mode. You can choose among the following 3 modalities:
     - rstp streamning (the continous stream that the user can play or pause by clicking the player buttons provided by the StreamViewer fragment)
     - still-image (you load a snapshot from the renmote camera by clicking on a button)
     - timed still-images (the system loads a jpeg image from the remote camera every # seconds, as specified by the user)
   
 * **PTZ_ControllerExample** This example explains you:
  - how to play a stream on a *StreamViewerFragment* 
  - how to get and/or update the properties of the stream by using a *StreamInspectorFragment*
  - how to remotely control pan, tilt and zoom values of an Axis PTZ Webcam by using a *PTZ_ControllerFragment*
  - how to make snapshots of the stream and save them into the internal storage
   
 * **PTZ_ImageGalleryExample** This example contains all the features of the *PTZ_ControllerExample* example, and in addition,  explains you: 
  - how to open an Image Gallery containing all the stream snaphots, by using a *ImageGalleryFragment* 
  - how to select an image from the gallery, zoom in/out and move it by touch screen gestures
  - how to delete an image from the gallery (simply by a double tap on it)
  
   
For running the Android examples, open your preferred IDE (e.g Eclipse) and do the following changes:
   - Import the Most-Streaming project library 
   - Edit the file *jni/Android.mk* and properly change the absolute path of the environment variables GSTREAMER_SDK_ROOT_ANDROID and GSTREAMER_ROOT 
   - Import the  Android project example located from the *android/examples* folder and add the Most-Streaming and the Most-Visualization  projects both as Library and project references 
   - Create your *uri.properties.default* property file and put it into the *assets* folder.(That folder already contains the *uri.properties* file that you can use as template for your own property file)
   - Build the projects (Note that the NDK must be installed and configurated on your system in order to build the project)
   - Deploy the application on an android device or emulator 
