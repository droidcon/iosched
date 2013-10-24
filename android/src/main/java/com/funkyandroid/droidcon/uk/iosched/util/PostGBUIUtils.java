package com.funkyandroid.droidcon.uk.iosched.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 24/10/2013
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
public class PostGBUIUtils {
    public boolean isTablet(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int minDp = Math.min(config.screenWidthDp, config.screenHeightDp);
        return minDp >= 600;
    }
}
