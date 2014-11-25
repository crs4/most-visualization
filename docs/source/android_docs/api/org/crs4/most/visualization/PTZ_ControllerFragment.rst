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

PTZ_ControllerFragment
======================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public class PTZ_ControllerFragment extends Fragment implements OnTouchListener

Methods
-------
newInstance
^^^^^^^^^^^

.. java:method:: public static PTZ_ControllerFragment newInstance()
   :outertype: PTZ_ControllerFragment

   Provides a new istance of this fragment

   :return: the PTZ_ControllerFragment instance

newInstance
^^^^^^^^^^^

.. java:method:: public static PTZ_ControllerFragment newInstance(boolean panTiltPanelVisible, boolean zoomPanelVisible, boolean snapShotVisible)
   :outertype: PTZ_ControllerFragment

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

