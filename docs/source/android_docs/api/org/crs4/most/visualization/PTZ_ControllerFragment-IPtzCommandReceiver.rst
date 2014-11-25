.. java:import:: java.util ArrayList

.. java:import:: org.crs4.most.streaming.enums PTZ_Direction

.. java:import:: org.crs4.most.streaming.enums PTZ_Zoom

.. java:import:: android.app Activity

.. java:import:: android.app Fragment

.. java:import:: android.os Bundle

.. java:import:: android.util Log

.. java:import:: android.view LayoutInflater

.. java:import:: android.view MotionEvent

.. java:import:: android.view View

.. java:import:: android.view View.OnClickListener

.. java:import:: android.view ViewGroup

.. java:import:: android.view View.OnTouchListener

.. java:import:: android.widget GridLayout

.. java:import:: android.widget ImageButton

PTZ_ControllerFragment.IPtzCommandReceiver
==========================================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public interface IPtzCommandReceiver
   :outertype: PTZ_ControllerFragment

   An activity must implement this interface to be able to receive notifications from the attached PTZ_ControllerFragment

Methods
-------
onGoHome
^^^^^^^^

.. java:method:: public void onGoHome()
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user clicks on the home button of the pan-tilt panel

onPTZstartMove
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartMove(PTZ_Direction dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user presses one button of the pan-tilt panel

   :param dir: the required moving direction

onPTZstartZoom
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartZoom(PTZ_Zoom dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user presses one button of the zoom panel

   :param dir: the required zooming direction

onPTZstopMove
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopMove(PTZ_Direction dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user releases one button of the pan-tilt panel

   :param the: moving direction before this stop command

onPTZstopZoom
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopZoom(PTZ_Zoom dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user releases one button of the zoom panel

   :param the: zooming direction before this stop command

onSnaphot
^^^^^^^^^

.. java:method:: public void onSnaphot()
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

   Called when the user clicks on the snapshot button

