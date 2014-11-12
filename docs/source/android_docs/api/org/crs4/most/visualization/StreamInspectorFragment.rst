.. java:import:: java.util List

.. java:import:: org.crs4.most.streaming IStream

.. java:import:: org.crs4.most.streaming StreamProperties

.. java:import:: org.crs4.most.streaming.enums StreamProperty

.. java:import:: android.app Activity

.. java:import:: android.app Dialog

.. java:import:: android.app Fragment

.. java:import:: android.graphics Color

.. java:import:: android.os Bundle

.. java:import:: android.util Log

.. java:import:: android.view LayoutInflater

.. java:import:: android.view View

.. java:import:: android.view ViewGroup

.. java:import:: android.view View.OnClickListener

.. java:import:: android.widget AdapterView

.. java:import:: android.widget ArrayAdapter

.. java:import:: android.widget Button

.. java:import:: android.widget EditText

.. java:import:: android.widget ListView

.. java:import:: android.widget AdapterView.OnItemClickListener

.. java:import:: android.widget TextView

StreamInspectorFragment
=======================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public class StreamInspectorFragment extends Fragment

   This fragment provides a way for visually getting real time informations about a list of \ :java:ref:`IStream`\  objects. Also, you can specify a filter for getting only the stream propertires you are interested in. You can attach this fragment to any activity, provided that it implements the \ :java:ref:`StreamInspectorFragment.IStreamProvider`\  interface.

Methods
-------
newInstance
^^^^^^^^^^^

.. java:method:: public static StreamInspectorFragment newInstance()
   :outertype: StreamInspectorFragment

   Provides a new istance of this fragment

   :return: the StreamInspectorFragment instance

onActivityCreated
^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void onActivityCreated(Bundle bundle)
   :outertype: StreamInspectorFragment

onAttach
^^^^^^^^

.. java:method:: @Override public void onAttach(Activity activity)
   :outertype: StreamInspectorFragment

onCreateView
^^^^^^^^^^^^

.. java:method:: @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   :outertype: StreamInspectorFragment

updateStreamStateInfo
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void updateStreamStateInfo(IStream stream)
   :outertype: StreamInspectorFragment

   This method would be called for notifying the StreamInspectorFragment that one or more properties of the IStream (specified as argument) has been changed,

   :param stream: the modified IStream object

