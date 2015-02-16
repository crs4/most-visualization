.. java:import:: java.util ArrayList

.. java:import:: org.crs4.most.streaming.enums PTZ_Direction

.. java:import:: org.crs4.most.streaming.enums PTZ_Zoom

.. java:import:: android.app ActionBar.LayoutParams

.. java:import:: android.content Context

.. java:import:: android.graphics.drawable BitmapDrawable

.. java:import:: android.util Log

.. java:import:: android.view Gravity

.. java:import:: android.view LayoutInflater

.. java:import:: android.view MotionEvent

.. java:import:: android.view View

.. java:import:: android.view View.OnClickListener

.. java:import:: android.view ViewGroup

.. java:import:: android.view View.OnTouchListener

.. java:import:: android.widget GridLayout

.. java:import:: android.widget ImageButton

.. java:import:: android.widget PopupWindow

.. java:import:: android.widget Toast

PTZ_ControllerPopupWindowFactory
================================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public class PTZ_ControllerPopupWindowFactory implements OnTouchListener

   This Factory class provides you an interactive visual panel containing a set of buttons to be used as a GUI frontend for handling remote PTZ webcams. You need to pass a \ :java:ref:`IPtzCommandReceiver`\  interface to the factory method of this class, because it notifies to this interface all the GUI actions (e.g button clicks) Note that the created window implements the \ :java:ref:`android.view.View.OnTouchListener`\  interface, so you can move it to the desired position on the screen.

Constructors
------------
PTZ_ControllerPopupWindowFactory
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public PTZ_ControllerPopupWindowFactory(Context context, IPtzCommandReceiver ptzReceiver, boolean panTiltPanelVisible, boolean zoomPanelVisible, boolean snapShotVisible, int xPos, int yPos)
   :outertype: PTZ_ControllerPopupWindowFactory

   Creates a new floating popupWindow, containing a set of optional panels to be viewed

   :param context: the context where to render the popup Window
   :param ptzReceiver: the remote object to use as the target of all user notifications
   :param panTiltPanelVisible: set the pan-tilt panel visible or not
   :param zoomPanelVisible: set the zoom panel visible or not
   :param snapShotVisible: set the snapshot button visible or not
   :param xPos: the initial X position of the popupWindow
   :param yPos: the initial y position of the popupWindow

Methods
-------
getPopupWindow
^^^^^^^^^^^^^^

.. java:method:: public PopupWindow getPopupWindow()
   :outertype: PTZ_ControllerPopupWindowFactory

   :return: the created popup Window

onTouch
^^^^^^^

.. java:method:: @Override public boolean onTouch(View v, MotionEvent event)
   :outertype: PTZ_ControllerPopupWindowFactory

show
^^^^

.. java:method:: public void show()
   :outertype: PTZ_ControllerPopupWindowFactory

   Show the popupWindow at the current location

