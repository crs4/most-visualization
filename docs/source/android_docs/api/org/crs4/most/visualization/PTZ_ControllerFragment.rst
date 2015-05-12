.. java:import:: java.util ArrayList

.. java:import:: org.crs4.most.streaming.enums PTZ_Direction

.. java:import:: org.crs4.most.streaming.enums PTZ_Zoom

.. java:import:: android.app Activity

.. java:import:: android.app DialogFragment

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

PTZ_ControllerFragment
======================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public class PTZ_ControllerFragment extends DialogFragment implements OnTouchListener

   This fragment provides you a set of visual panels and buttons to be used as a GUI frontend for handling remote PTZ webcams. This fragment expects the attached activity implements the \ :java:ref:`IPtzCommandReceiver`\  interface, because it notifies to this interface all the GUI actions (e.g button clicks)

Methods
-------
newInstance
^^^^^^^^^^^

.. java:method:: public static PTZ_ControllerFragment newInstance()
   :outertype: PTZ_ControllerFragment

   Provides a new instance of this fragment, with all panels visible

   :return: the PTZ_ControllerFragment instance

newInstance
^^^^^^^^^^^

.. java:method:: public static PTZ_ControllerFragment newInstance(boolean panTiltPanelVisible, boolean zoomPanelVisible, boolean snapShotVisible)
   :outertype: PTZ_ControllerFragment

   Provides a new instance of this fragment, with a selection of desired panels

   :param panTiltPanelVisible: set the pan-tilt panel visible or not
   :param zoomPanelVisible: set the zoom panel visible or not
   :param snapShotVisible: set the snapshot button visible or not

onAttach
^^^^^^^^

.. java:method:: @Override public void onAttach(Activity activity)
   :outertype: PTZ_ControllerFragment

onCreateView
^^^^^^^^^^^^

.. java:method:: @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   :outertype: PTZ_ControllerFragment

onTouch
^^^^^^^

.. java:method:: @Override public boolean onTouch(View v, MotionEvent event)
   :outertype: PTZ_ControllerFragment

