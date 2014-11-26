.. java:import:: java.util List

.. java:import:: org.crs4.most.streaming IStream

.. java:import:: org.crs4.most.streaming.enums StreamProperty

.. java:import:: org.crs4.most.streaming.enums StreamState

.. java:import:: android.content Context

.. java:import:: android.graphics Color

.. java:import:: android.view LayoutInflater

.. java:import:: android.view View

.. java:import:: android.view ViewGroup

.. java:import:: android.widget ArrayAdapter

.. java:import:: android.widget TextView

IStreamArrayAdapter
===================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type::  class IStreamArrayAdapter extends ArrayAdapter<IStream>

   This adapter is internally used from the \ :java:ref:`StreamInspectorFragment`\  for representing IStream data.

Constructors
------------
IStreamArrayAdapter
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public IStreamArrayAdapter(Context context, int viewId, List<IStream> objects, List<StreamProperty> streamProperties)
   :outertype: IStreamArrayAdapter

   This adapter provides a way of rendering informations about a list of \ :java:ref:`IStream`\  objects.

   :param context:
   :param viewId: the view id where to render the informations about each stream
   :param objects: the list of \ :java:ref:`IStream`\  objects.
   :param streamProperties: the properties to render for each stream (a null value renders all the available properties)

Methods
-------
getView
^^^^^^^

.. java:method:: @Override public View getView(int position, View convertView, ViewGroup parent)
   :outertype: IStreamArrayAdapter

