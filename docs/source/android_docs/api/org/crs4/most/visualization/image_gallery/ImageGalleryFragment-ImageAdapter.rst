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

.. java:import:: android.view View.OnLongClickListener

.. java:import:: android.view ViewGroup

.. java:import:: android.view ViewGroup.LayoutParams

.. java:import:: android.widget AdapterView

.. java:import:: android.widget AdapterView.OnItemClickListener

.. java:import:: android.widget BaseAdapter

.. java:import:: android.widget Gallery

.. java:import:: android.widget ImageView

.. java:import:: android.widget LinearLayout

.. java:import:: android.widget Toast

ImageGalleryFragment.ImageAdapter
=================================

.. java:package:: org.crs4.most.visualization.image_gallery
   :noindex:

.. java:type:: public class ImageAdapter extends BaseAdapter
   :outertype: ImageGalleryFragment

Fields
------
imageBackground
^^^^^^^^^^^^^^^

.. java:field::  int imageBackground
   :outertype: ImageGalleryFragment.ImageAdapter

Constructors
------------
ImageAdapter
^^^^^^^^^^^^

.. java:constructor:: public ImageAdapter(Context c)
   :outertype: ImageGalleryFragment.ImageAdapter

Methods
-------
getCount
^^^^^^^^

.. java:method:: @Override public int getCount()
   :outertype: ImageGalleryFragment.ImageAdapter

getItem
^^^^^^^

.. java:method:: @Override public Object getItem(int arg0)
   :outertype: ImageGalleryFragment.ImageAdapter

getItemId
^^^^^^^^^

.. java:method:: @Override public long getItemId(int arg0)
   :outertype: ImageGalleryFragment.ImageAdapter

getView
^^^^^^^

.. java:method:: @Override public View getView(int arg0, View arg1, ViewGroup arg2)
   :outertype: ImageGalleryFragment.ImageAdapter

