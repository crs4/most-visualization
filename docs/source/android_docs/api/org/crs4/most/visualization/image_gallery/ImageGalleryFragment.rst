.. java:import:: java.io File

.. java:import:: org.crs4.most.streaming.utils ImageDownloader

.. java:import:: org.crs4.most.visualization R

.. java:import:: android.app AlertDialog

.. java:import:: android.app Fragment

.. java:import:: android.content Context

.. java:import:: android.content DialogInterface

.. java:import:: android.content.res TypedArray

.. java:import:: android.graphics Color

.. java:import:: android.graphics.drawable Drawable

.. java:import:: android.os Bundle

.. java:import:: android.util Log

.. java:import:: android.view GestureDetector

.. java:import:: android.view Gravity

.. java:import:: android.view LayoutInflater

.. java:import:: android.view MotionEvent

.. java:import:: android.view View

.. java:import:: android.view ViewGroup

.. java:import:: android.view ViewGroup.LayoutParams

.. java:import:: android.widget AdapterView

.. java:import:: android.widget AdapterView.OnItemClickListener

.. java:import:: android.widget BaseAdapter

.. java:import:: android.widget Gallery

.. java:import:: android.widget ImageView

.. java:import:: android.widget LinearLayout

ImageGalleryFragment
====================

.. java:package:: org.crs4.most.visualization.image_gallery
   :noindex:

.. java:type:: public class ImageGalleryFragment extends Fragment

   This fragment allows you to embed in your activity an image gallery.

Fields
------
imageView
^^^^^^^^^

.. java:field::  LinearLayout imageView
   :outertype: ImageGalleryFragment

Methods
-------
onActivityCreated
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onActivityCreated(Bundle savedInstanceState)
   :outertype: ImageGalleryFragment

onCreateView
^^^^^^^^^^^^

.. java:method:: @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   :outertype: ImageGalleryFragment

   Called when the activity is first created.

reloadGalleryImages
^^^^^^^^^^^^^^^^^^^

.. java:method:: public void reloadGalleryImages()
   :outertype: ImageGalleryFragment

   Reloads the images contained in to the internal storage

selectImage
^^^^^^^^^^^

.. java:method:: public void selectImage(int imageIndex)
   :outertype: ImageGalleryFragment

   Select an image from the gallery, by index array

   :param imageIndex: the index of the image (the index 0 is the newest image)

