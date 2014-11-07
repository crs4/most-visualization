.. java:import:: android.view SurfaceView

IStreamFragmentCommandListener
==============================

.. java:package:: org.crs4.most.visualization
   :noindex:

.. java:type:: public interface IStreamFragmentCommandListener

Methods
-------
onPause
^^^^^^^

.. java:method:: public void onPause(String streamId)
   :outertype: IStreamFragmentCommandListener

   Callback triggered when the user clicks on the pause button

   :param streamId: the id of the stream the StreamFragment refer to

onPlay
^^^^^^

.. java:method:: public void onPlay(String streamId)
   :outertype: IStreamFragmentCommandListener

   Callback triggered after the user clicked on the play button

   :param streamId: the id of the stream the \ :java:ref:`StreamViewerFragment`\  refer to

onSurfaceViewCreated
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView)
   :outertype: IStreamFragmentCommandListener

   Callback triggered once the surfaceView of the fragment became available

   :param streamId: the id of the stream the \ :java:ref:`StreamViewerFragment`\  refer to
   :param surfaceView: the surfaceView where to render the stream

onSurfaceViewDestroyed
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void onSurfaceViewDestroyed(String streamId)
   :outertype: IStreamFragmentCommandListener

   Callback triggered after the surfaceView of this fragment has been destroyed

   :param streamId: the id of the stream the \ :java:ref:`StreamViewerFragment`\  refer to

