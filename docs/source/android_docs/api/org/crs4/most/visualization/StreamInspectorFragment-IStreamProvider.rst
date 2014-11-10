.. java:import:: java.util List

.. java:import:: org.crs4.most.streaming IStream

.. java:import:: org.crs4.most.streaming StreamProperties

.. java:import:: org.crs4.most.streaming.enums StreamProperty

.. java:import:: android.app Activity

.. java:import:: android.app Dialog

.. java:import:: android.app Fragment

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

StreamInspectorFragment.IStreamProvider
=======================================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public interface IStreamProvider
   :outertype: StreamInspectorFragment

   This interface is used by the StreamInspector for getting the streams to inspect along with their properties.

Methods
-------
getStreamProperties
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<StreamProperty> getStreamProperties()
   :outertype: StreamInspectorFragment.IStreamProvider

   Provide the list of properties to show for each stream (a null value shows all properties)

getStreams
^^^^^^^^^^

.. java:method:: public List<IStream> getStreams()
   :outertype: StreamInspectorFragment.IStreamProvider

   Provides the list of the streams to show in the inspector

   :return: the list of the streams to inspect

