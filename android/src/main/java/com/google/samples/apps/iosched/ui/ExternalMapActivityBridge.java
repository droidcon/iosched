package com.google.samples.apps.iosched.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.samples.apps.iosched.BuildConfig;

/**
 * The IOsched Activities weren't designed to persist between call-outs to other applications. This
 * activity is designed to call out to the external Maps app and then re-start the previous activity
 * when the user returns.
 */
public class ExternalMapActivityBridge extends Activity {

    public static final String EXTRA_ORIGINAL_INTENT = "original_intent";

    private static final String TAG = "ExternalMappingActivity";

    private boolean mIsCallingOut = true;

    private Intent mIntentUserToStartPreviousActivity;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);

        mIsCallingOut = true;

        mIntentUserToStartPreviousActivity = getIntent().getParcelableExtra(EXTRA_ORIGINAL_INTENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsCallingOut) {
            startExternalMappingApp();
            mIsCallingOut = false;
        } else {
            restartOriginalActivity();
            finish();
        }
    }

    private void startExternalMappingApp() {
        Uri locationUri = constructExternalMappingAppUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
        if(intent.resolveActivity(getPackageManager()) == null) {
            restartOriginalActivity();
        } else {
            startActivity(intent);
        }
    }

    private Uri constructExternalMappingAppUri() {
        String uri = "geo:0,0?q="
                + BuildConfig.VENUE_LATITUDE + "," + BuildConfig.VENUE_LONGITUDE
                + "("+BuildConfig.CONFERENCE_NAME+")";
        return Uri.parse(uri);
    }

    private void restartOriginalActivity() {
        Intent startIntent;

        String previousActivityClassName =
                mIntentUserToStartPreviousActivity.getComponent().getClassName();
        Class previousActivityClass;
        try {
            previousActivityClass = Class.forName(previousActivityClassName);
            startIntent =new Intent(this, previousActivityClass);
            Bundle extras = mIntentUserToStartPreviousActivity.getExtras();
            if(extras != null) {
                startIntent.putExtras(extras);
            }
        } catch(ClassNotFoundException e) {
            Log.e(TAG, "Unable to find class for previous activity ("
                    + previousActivityClassName
                    + "), starting default launcher activity instead.");
            startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        startActivity(startIntent);
        finish();
    }


}
