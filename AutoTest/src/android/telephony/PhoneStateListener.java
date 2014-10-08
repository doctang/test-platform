/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.telephony;

/**
 * A listener class for monitoring changes in specific telephony states
 * on the device, including service state, signal strength, message
 * waiting indicator (voicemail), and others.
 * <p>
 * Override the methods for the state that you wish to receive updates for, and
 * pass your PhoneStateListener object, along with bitwise-or of the LISTEN_
 * flags to {@link TelephonyManager#listen TelephonyManager.listen()}.
 * <p>
 * Note that access to some telephony information is
 * permission-protected. Your application won't receive updates for protected
 * information unless it has the appropriate permissions declared in
 * its manifest file. Where permissions apply, they are noted in the
 * appropriate LISTEN_ flags.
 */
public class PhoneStateListener {

    /**
     * Stop listening for updates.
     */
    public static final int LISTEN_NONE = 0;

    /**
     *  Listen for changes to the network service state (cellular).
     *
     *  @see #onServiceStateChanged
     *  @see ServiceState
     */
    public static final int LISTEN_SERVICE_STATE                            = 0x00000001;

    /**
     * Listen for changes to the network signal strength (cellular).
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
     * READ_PHONE_STATE}
     * <p>
     *
     * @see #onSignalStrengthChanged
     *
     * @deprecated by {@link #LISTEN_SIGNAL_STRENGTHS}
     */
    @Deprecated
    public static final int LISTEN_SIGNAL_STRENGTH                          = 0x00000002;

    /**
     * Listen for changes to the message-waiting indicator.
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
     * READ_PHONE_STATE}
     * <p>
     * Example: The status bar uses this to determine when to display the
     * voicemail icon.
     *
     * @see #onMessageWaitingIndicatorChanged
     */
    public static final int LISTEN_MESSAGE_WAITING_INDICATOR                = 0x00000004;

    /**
     * Listen for changes to the call-forwarding indicator.
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
     * READ_PHONE_STATE}
     * @see #onCallForwardingIndicatorChanged
     */
    public static final int LISTEN_CALL_FORWARDING_INDICATOR                = 0x00000008;

    /**
     * Listen for changes to the device's cell location. Note that
     * this will result in frequent callbacks to the listener.
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#ACCESS_COARSE_LOCATION
     * ACCESS_COARSE_LOCATION}
     * <p>
     * If you need regular location updates but want more control over
     * the update interval or location precision, you can set up a listener
     * through the {@link android.location.LocationManager location manager}
     * instead.
     *
     * @see #onCellLocationChanged
     */
    public static final int LISTEN_CELL_LOCATION                            = 0x00000010;

    /**
     * Listen for changes to the device call state.
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
     * READ_PHONE_STATE}
     * @see #onCallStateChanged
     */
    public static final int LISTEN_CALL_STATE                               = 0x00000020;

    /**
     * Listen for changes to the data connection state (cellular).
     *
     * @see #onDataConnectionStateChanged
     */
    public static final int LISTEN_DATA_CONNECTION_STATE                    = 0x00000040;

    /**
     * Listen for changes to the direction of data traffic on the data
     * connection (cellular).
     * {@more}
     * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
     * READ_PHONE_STATE}
     * Example: The status bar uses this to display the appropriate
     * data-traffic icon.
     *
     * @see #onDataActivity
     */
    public static final int LISTEN_DATA_ACTIVITY                            = 0x00000080;

    /**
     * Listen for changes to the network signal strengths (cellular).
     * <p>
     * Example: The status bar uses this to control the signal-strength
     * icon.
     *
     * @see #onSignalStrengthsChanged
     */
    public static final int LISTEN_SIGNAL_STRENGTHS                         = 0x00000100;

    /**
     * Listen for changes to OTASP mode.
     *
     * @see #onOtaspChanged
     * @hide
     */
    public static final int LISTEN_OTASP_CHANGED                            = 0x00000200;

    public PhoneStateListener() {
    }

    public PhoneStateListener(int subscription) {
    }

    /**
     * Callback invoked when device service state changes.
     *
     * @see ServiceState#STATE_EMERGENCY_ONLY
     * @see ServiceState#STATE_IN_SERVICE
     * @see ServiceState#STATE_OUT_OF_SERVICE
     * @see ServiceState#STATE_POWER_OFF
     */
    public void onServiceStateChanged(ServiceState serviceState) {
        // default implementation empty
    }

    /**
     * Callback invoked when network signal strength changes.
     *
     * @see ServiceState#STATE_EMERGENCY_ONLY
     * @see ServiceState#STATE_IN_SERVICE
     * @see ServiceState#STATE_OUT_OF_SERVICE
     * @see ServiceState#STATE_POWER_OFF
     * @deprecated Use {@link #onSignalStrengthsChanged(SignalStrength)}
     */
    @Deprecated
    public void onSignalStrengthChanged(int asu) {
        // default implementation empty
    }

    /**
     * Callback invoked when the message-waiting indicator changes.
     */
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        // default implementation empty
    }

    /**
     * Callback invoked when the call-forwarding indicator changes.
     */
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        // default implementation empty
    }

    /**
     * Callback invoked when device cell location changes.
     */
    public void onCellLocationChanged(CellLocation location) {
        // default implementation empty
    }

    /**
     * Callback invoked when device call state changes.
     *
     * @see TelephonyManager#CALL_STATE_IDLE
     * @see TelephonyManager#CALL_STATE_RINGING
     * @see TelephonyManager#CALL_STATE_OFFHOOK
     */
    public void onCallStateChanged(int state, String incomingNumber) {
        // default implementation empty
    }

    /**
     * Callback invoked when connection state changes.
     *
     * @see TelephonyManager#DATA_DISCONNECTED
     * @see TelephonyManager#DATA_CONNECTING
     * @see TelephonyManager#DATA_CONNECTED
     * @see TelephonyManager#DATA_SUSPENDED
     */
    public void onDataConnectionStateChanged(int state) {
        // default implementation empty
    }

    /**
     * same as above, but with the network type.  Both called.
     */
    public void onDataConnectionStateChanged(int state, int networkType) {
    }

    /**
     * Callback invoked when data activity state changes.
     *
     * @see TelephonyManager#DATA_ACTIVITY_NONE
     * @see TelephonyManager#DATA_ACTIVITY_IN
     * @see TelephonyManager#DATA_ACTIVITY_OUT
     * @see TelephonyManager#DATA_ACTIVITY_INOUT
     * @see TelephonyManager#DATA_ACTIVITY_DORMANT
     */
    public void onDataActivity(int direction) {
        // default implementation empty
    }

    /**
     * Callback invoked when network signal strengths changes.
     *
     * @see ServiceState#STATE_EMERGENCY_ONLY
     * @see ServiceState#STATE_IN_SERVICE
     * @see ServiceState#STATE_OUT_OF_SERVICE
     * @see ServiceState#STATE_POWER_OFF
     */
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        // default implementation empty
    }


    /**
     * The Over The Air Service Provisioning (OTASP) has changed. Requires
     * the READ_PHONE_STATE permission.
     * @param otaspMode is integer <code>OTASP_UNKNOWN=1<code>
     *   means the value is currently unknown and the system should wait until
     *   <code>OTASP_NEEDED=2<code> or <code>OTASP_NOT_NEEDED=3<code> is received before
     *   making the decisision to perform OTASP or not.
     *
     * @hide
     */
    public void onOtaspChanged(int otaspMode) {
        // default implementation empty
    }
}
