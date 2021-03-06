package com.android.sdk.qrcode;

import android.util.Log;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2019-04-02 12:55
 */
class Debug {

    private static final String TAG = "QRCODE";
    private static boolean debug = BuildConfig.DEBUG;

    static void setDebug(boolean debug) {
        Debug.debug = debug;
    }

    static void log(String msg) {
        if (debug && msg != null) {
            Log.d(TAG, msg);
        }
    }

}
