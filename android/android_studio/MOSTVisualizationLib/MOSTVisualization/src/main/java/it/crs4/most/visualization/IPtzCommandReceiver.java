/*!
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014-15, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.visualization;

import it.crs4.most.streaming.enums.PTZ_Direction;
import it.crs4.most.streaming.enums.PTZ_Zoom;

/**
 * An activity must implement this interface to be able to receive notifications from the attached PTZ_ControllerFragment or PTZ_PopupWindow
 */
public interface IPtzCommandReceiver {

    /**
     * Called when the user presses one button of the pan-tilt panel
     *
     * @param dir the required moving direction
     */
    public void onPTZstartMove(PTZ_Direction dir);

    /**
     * Called when the user releases one button of the pan-tilt panel
     *
     * @param the moving direction before this stop command
     */
    public void onPTZstopMove(PTZ_Direction dir);

    /**
     * Called when the user presses one button of the zoom panel
     *
     * @param dir the required zooming direction
     */
    public void onPTZstartZoom(PTZ_Zoom dir);

    /**
     * Called when the user releases one button of the zoom panel
     *
     * @param the zooming direction before this stop command
     */
    public void onPTZstopZoom(PTZ_Zoom dir);

    /**
     * Called when the user clicks on the home button of the pan-tilt panel
     */
    public void onGoHome();

    /**
     * Called when the user clicks on the snapshot button
     */
    public void onSnapshot();

}
