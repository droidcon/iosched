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

package com.funkyandroid.droidcon.uk.iosched.io;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Color;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TracksResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TrackResponse;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleContract;
import com.funkyandroid.droidcon.uk.iosched.util.Lists;
import com.funkyandroid.droidcon.uk.iosched.util.ParserUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.makeLogTag;

public class TracksHandler extends JSONHandler {

    private static final String TAG = makeLogTag(TracksHandler.class);

    public TracksHandler(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(String json)
            throws IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
        batch.add(ContentProviderOperation.newDelete(
                ScheduleContract.addCallerIsSyncAdapterParameter(
                        ScheduleContract.Tracks.CONTENT_URI)).build());
        TracksResponse response = new TracksResponse();
        try {
            response.fromJSON(new JSONObject(json));
        } catch(JSONException e) {
            return batch;
        }

        for(TrackResponse track : response.getTracks()) {
            parseTrack(track, batch);
        }

        return batch;
    }

    private static void parseTrack(TrackResponse track, ArrayList<ContentProviderOperation> batch) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                ScheduleContract.addCallerIsSyncAdapterParameter(
                        ScheduleContract.Tracks.CONTENT_URI));
        builder.withValue(ScheduleContract.Tracks.TRACK_ID, track.getId());
        builder.withValue(ScheduleContract.Tracks.TRACK_NAME, track.getTitle());
        builder.withValue(ScheduleContract.Tracks.TRACK_COLOR, Color.parseColor(track.getColour()));
        builder.withValue(ScheduleContract.Tracks.TRACK_ABSTRACT, track.getDescription());
        builder.withValue(ScheduleContract.Tracks.TRACK_META, track.getMeta());
        builder.withValue(ScheduleContract.Tracks.TRACK_HASHTAG, ParserUtils.sanitizeId(track.getTitle()));
        batch.add(builder.build());
    }
}
