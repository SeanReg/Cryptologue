package com.teamsynergy.cryptologue;

import android.content.Context;

import com.parse.Parse;

/**
 * Created by Sean on 3/23/2017.
 */

public class ParseInit {
    private static boolean mInitialized = false;

    public static void start(Context con) {
        if (!mInitialized) {
            mInitialized = true;
            //Intialize access to the Parse server
            Parse.initialize(new Parse.Configuration.Builder(con)
                    .applicationId("I6DUDtMs1nxNl3w4MA794RSqBGmgPl7gnlJrjrtW")
                    .clientKey("fqi3Z155yA0xr2uZoN6SBkdh9zvlXVedDE6aEYUQ")
                    .server("https://cryptologue.back4app.io/").enableLocalDataStore().build()
            );

            AccountManager.initialize(con);
        }
    }
}
