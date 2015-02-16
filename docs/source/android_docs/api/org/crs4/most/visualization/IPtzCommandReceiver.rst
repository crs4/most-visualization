.. java:import:: org.crs4.most.streaming.enums PTZ_Direction

.. java:import:: org.crs4.most.streaming.enums PTZ_Zoom

IPtzCommandReceiver
===================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public interface IPtzCommandReceiver

   An activity must implement this interface to be able to receive notifications from the attached PTZ_ControllerFragment or PTZ_PopupWindow

Methods
-------
onGoHome
^^^^^^^^

.. java:method:: public void onGoHome()
   :outertype: IPtzCommandReceiver

   Called when the user clicks on the home button of the pan-tilt panel

onPTZstartMove
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartMove(PTZ_Direction dir)
   :outertype: IPtzCommandReceiver

   Called when the user presses one button of the pan-tilt panel

   :param dir: the required moving direction

onPTZstartZoom
^^^^^^^^^^^^^^

.. java:method:: public void onPTZstartZoom(PTZ_Zoom dir)
   :outertype: IPtzCommandReceiver

   Called when the user presses one button of the zoom panel

   :param dir: the required zooming direction

onPTZstopMove
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopMove(PTZ_Direction dir)
   :outertype: IPtzCommandReceiver

   Called when the user releases one button of the pan-tilt panel

   :param the: moving direction before this stop command

onPTZstopZoom
^^^^^^^^^^^^^

.. java:method:: public void onPTZstopZoom(PTZ_Zoom dir)
   :outertype: IPtzCommandReceiver

   Called when the user releases one button of the zoom panel

   :param the: zooming direction before this stop command

onSnaphot
^^^^^^^^^

.. java:method:: public void onSnaphot()
   :outertype: IPtzCommandReceiver

   Called when the user clicks on the snapshot button

