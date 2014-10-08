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

package android.net;

/**
 * Class that answers queries about the state of network connectivity. It also
 * notifies applications when network connectivity changes. Get an instance
 * of this class by calling
 * {@link android.content.Context#getSystemService(String) Context.getSystemService(Context.CONNECTIVITY_SERVICE)}.
 * <p>
 * The primary responsibilities of this class are to:
 * <ol>
 * <li>Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)</li>
 * <li>Send broadcast intents when network connectivity changes</li>
 * <li>Attempt to "fail over" to another network when connectivity to a network
 * is lost</li>
 * <li>Provide an API that allows applications to query the coarse-grained or fine-grained
 * state of the available networks</li>
 * </ol>
 */
public class ConnectivityManager {

    /**
     * Gets the value of the setting for enabling Mobile data.
     *
     * @return Whether mobile data is enabled.
     * @hide
     */
    public boolean getMobileDataEnabled() {
        return false;
    }

    /**
     * Sets the persisted value for enabling/disabling Mobile data.
     *
     * @param enabled Whether the mobile data connection should be
     *            used or not.
     * @hide
     */
    public void setMobileDataEnabled(boolean enabled) {
    }
}
