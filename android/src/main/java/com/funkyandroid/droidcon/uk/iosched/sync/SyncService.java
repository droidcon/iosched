/*
 * Copyright 2012 Google Inc.
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

package com.funkyandroid.droidcon.uk.iosched.sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import com.funkyandroid.droidcon.uk.iosched.Config;
import com.funkyandroid.droidcon.uk.iosched.util.UIUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.LOGE;

/**
 * Service that handles sync. We're not requiring users to have any kind of account, so this needs to perform
 * the sync rather than just be a front end to a SyncAdapter.
 */
public class SyncService extends IntentService {

    private SyncHelper mSyncHelper;

    private static PendingIntent alarmIntent = null;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if(alarmIntent != null) {
            ((AlarmManager)getSystemService(Context.ALARM_SERVICE)).cancel(alarmIntent);
            alarmIntent = null;
        }

        Bundle extras = intent.getExtras();
        final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);

        if (!uploadOnly) {
            downloadFromServer();
        }

        queueNextSync();
    }

    /**
     * Download data from the server.
     */

    private void downloadFromServer() {
        // Perform a sync using SyncHelper
        if (mSyncHelper == null) {
            mSyncHelper = new SyncHelper(this);
        }

        // Dummy SyncResult object, we can use this for detecting issues
        // at some point.
        SyncResult syncResult = new SyncResult();
        try {
            mSyncHelper.performSync(syncResult,
                    SyncHelper.FLAG_SYNC_LOCAL | SyncHelper.FLAG_SYNC_REMOTE);

        } catch (IOException e) {
            ++syncResult.stats.numIoExceptions;
            LOGE(Config.LOG_TAG, "Error syncing data for Droidcon London 2013.", e);
        }
    }

    /**
     * Queue up the next sync
     */

    private void queueNextSync() {
        Calendar nowCalendar = Calendar.getInstance();
        long now = nowCalendar.getTimeInMillis();

        // No auto-sync once the conference is over
        if(now > UIUtils.CONFERENCE_END_MILLIS) {
            return;
        }

        long syncInterval;
        if(now < UIUtils.CONFERENCE_START_MILLIS - 48*60*60*1000) {
            // More than two days ahead; Sync once per day
            syncInterval = 24*60*60*1000;
        } else if ( now < UIUtils.CONFERENCE_START_MILLIS ) {
            // If we're in the last 2 days before the conference
            // step the sync up to twice per day.
            syncInterval = Math.min(12*60*60*1000, UIUtils.CONFERENCE_START_MILLIS - now);
        } else {
            // During the conference sync every 30 mins between 7am and 7pm in the Timezone of the conference,
            // and outside of that don't sync.
            Calendar syncTimeCalendar = Calendar.getInstance();
            syncTimeCalendar.setTimeZone(UIUtils.CONFERENCE_TIME_ZONE);
            if(nowCalendar.get(Calendar.HOUR_OF_DAY) < 7 ) {
                // before 7am set the next sync to 7am that day
                syncTimeCalendar.set(Calendar.HOUR_OF_DAY, 7);
                syncInterval = syncTimeCalendar.getTimeInMillis() - now;
            } else if(nowCalendar.get(Calendar.HOUR_OF_DAY) > 19) {
                // after 7pm set the next sync to 7am the next day
                syncTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
                syncTimeCalendar.set(Calendar.HOUR_OF_DAY, 7);
                syncInterval = syncTimeCalendar.getTimeInMillis() - now;
            } else {
                syncInterval = 30*60*1000;
            }
        }

        // And now for some fuzz to make sure all of the ponies don't
        // ride over the server together. 15 mins either way is good.
        syncInterval += ((new Random().nextInt(30))-15)*60*1000;

        // Queue the next alarm
        alarmIntent = PendingIntent.getService(this, 0, new Intent(this,SyncService.class), 0);
        ((AlarmManager)getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC,System.currentTimeMillis()+syncInterval,alarmIntent);
    }

}
