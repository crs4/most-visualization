/*!
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package org.crs4.most.visualization;



import android.view.SurfaceView;

public interface IStreamFragmentCommandListener {

	/**
	 * Callback triggered after the user clicked on the play button
	 * @param streamId the id of the stream the {@link StreamViewerFragment} refer to
	 */
	public void onPlay(String streamId);
	
	/**
	 * Callback triggered when the user clicks on the pause button
	 * @param streamId the id of the stream the StreamFragment refer to
	 */
	public void onPause(String streamId);
	
	/**
	 * Callback triggered once the surfaceView of the fragment became available 
	 * @param streamId  the id of the stream the {@link StreamViewerFragment} refer to
	 * @param surfaceView the surfaceView where to render the stream
	 */
	public void onSurfaceViewCreated(String streamId, SurfaceView surfaceView);
	
	/**
	 * Callback triggered after the surfaceView of this fragment has been destroyed 
	 * @param streamId the id of the stream the {@link StreamViewerFragment} refer to
	 */
	public void onSurfaceViewDestroyed(String streamId);
}
