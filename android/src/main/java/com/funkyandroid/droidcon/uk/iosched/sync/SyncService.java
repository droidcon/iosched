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

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import com.funkyandroid.droidcon.uk.iosched.Config;

import java.io.IOException;

import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.LOGE;
import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.LOGI;

/**
 * Service that handles sync. We're not requiring users to have any kind of account, so this needs to perform
 * the sync rather than just be a front end to a SyncAdapter.
 */
public class SyncService extends IntentService {

    private SyncHelper mSyncHelper;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        final boolean uploadOnly = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);
        final boolean manualSync = extras.getBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        final boolean initialize = extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);

        if (uploadOnly) {
            return;
        }

        LOGI(Config.LOG_TAG, "Beginning sync ," +
                " uploadOnly=" + uploadOnly +
                " manualSync=" + manualSync +
                " initialize=" + initialize);

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
}
