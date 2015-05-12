.. java:import:: org.crs4.most.streaming IStream

.. java:import:: android.os Bundle

.. java:import:: android.app Activity

.. java:import:: android.app Fragment

.. java:import:: android.util Log

.. java:import:: android.view LayoutInflater

.. java:import:: android.view SurfaceView

.. java:import:: android.view View

.. java:import:: android.view ViewGroup

.. java:import:: android.view View.OnClickListener

.. java:import:: android.widget ImageButton

StreamViewerFragment
====================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public class StreamViewerFragment extends Fragment

   This fragment represents a visual container for an \ :java:ref:`IStream`\ . It can be attached to any Activity, provided that it implements the \ :java:ref:`IStreamFragmentCommandListener`\  interface. This fragment contains a surface where to render the stream along with two image buttons that you can optionally use for sending play or pause stream requests to the attached activity

Fields
------
FRAGMENT_STREAM_ID_KEY
^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String FRAGMENT_STREAM_ID_KEY
   :outertype: StreamViewerFragment

Methods
-------
newInstance
^^^^^^^^^^^

.. java:method:: public static StreamViewerFragment newInstance(String streamId)
   :outertype: StreamViewerFragment

   Intances a new StreamViewerFragment

   :param streamId: the id of the stream to render
   :return: a new StreamViewerFragment instance

onActivityCreated
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onActivityCreated(Bundle bundle)
   :outertype: StreamViewerFragment

onAttach
^^^^^^^^

.. java:method:: @Override public void onAttach(Activity activity)
   :outertype: StreamViewerFragment

onCreate
^^^^^^^^

.. java:method:: @Override public void onCreate(Bundle savedInstanceState)
   :outertype: StreamViewerFragment

onCreateView
^^^^^^^^^^^^

.. java:method:: @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   :outertype: StreamViewerFragment

onDetach
^^^^^^^^

.. java:method:: @Override public void onDetach()
   :outertype: StreamViewerFragment

setPlayerButtonsVisible
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void setPlayerButtonsVisible(boolean value)
   :outertype: StreamViewerFragment

   Set the player buttons visible or not

   :param value: \ ``true``\  set buttons visible; \ ``false``\  invisible.

