.. java:import:: android.content Context

.. java:import:: android.graphics Bitmap

.. java:import:: android.graphics Matrix

.. java:import:: android.graphics PointF

.. java:import:: android.util FloatMath

.. java:import:: android.util Log

.. java:import:: android.view GestureDetector

.. java:import:: android.view MotionEvent

.. java:import:: android.view View

.. java:import:: android.view View.OnLongClickListener

.. java:import:: android.widget ImageView

.. java:import:: android.widget Toast

TouchImageView
==============

.. java:package:: org.crs4.most.visualization.image_gallery
   :noindex:

.. java:type:: public class TouchImageView extends ImageView

Fields
------
DRAG
^^^^

.. java:field:: static final int DRAG
   :outertype: TouchImageView

NONE
^^^^

.. java:field:: static final int NONE
   :outertype: TouchImageView

ZOOM
^^^^

.. java:field:: static final int ZOOM
   :outertype: TouchImageView

context
^^^^^^^

.. java:field::  Context context
   :outertype: TouchImageView

matrix
^^^^^^

.. java:field::  Matrix matrix
   :outertype: TouchImageView

mid
^^^

.. java:field::  PointF mid
   :outertype: TouchImageView

mode
^^^^

.. java:field::  int mode
   :outertype: TouchImageView

oldDist
^^^^^^^

.. java:field::  float oldDist
   :outertype: TouchImageView

savedMatrix
^^^^^^^^^^^

.. java:field::  Matrix savedMatrix
   :outertype: TouchImageView

start
^^^^^

.. java:field::  PointF start
   :outertype: TouchImageView

Constructors
------------
TouchImageView
^^^^^^^^^^^^^^

.. java:constructor:: public TouchImageView(Context context, GestureDetector gestureDetector)
   :outertype: TouchImageView

Methods
-------
onTouchEvent
^^^^^^^^^^^^

.. java:method:: @Override public boolean onTouchEvent(MotionEvent event)
   :outertype: TouchImageView

setImage
^^^^^^^^

.. java:method:: public void setImage(Bitmap bm, int displayWidth, int displayHeight)
   :outertype: TouchImageView

