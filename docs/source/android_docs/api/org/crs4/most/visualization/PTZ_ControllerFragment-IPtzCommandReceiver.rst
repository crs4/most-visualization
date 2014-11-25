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

Methods
-------
onGoHome
^^^^^^^^

.. java:method:: public void onGoHome()
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

onPTZstartMove
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartMove(PTZ_Direction dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

onPTZstartZoom
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartZoom(PTZ_Zoom dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

onPTZstopMove
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopMove(PTZ_Direction dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

onPTZstopZoom
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopZoom(PTZ_Zoom dir)
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

onSnaphot
^^^^^^^^^

.. java:method:: public void onSnaphot()
   :outertype: PTZ_ControllerFragment.IPtzCommandReceiver

