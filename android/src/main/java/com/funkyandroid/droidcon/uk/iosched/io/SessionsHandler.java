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
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.funkyandroid.droidcon.uk.droidconsched.io.ConferenceAPI;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.SessionResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.SessionsResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TrackResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TracksResponse;
import com.funkyandroid.droidcon.uk.iosched.Config;
import com.funkyandroid.droidcon.uk.iosched.R;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleContract;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleContract.Sessions;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleContract.SyncColumns;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleDatabase;
import com.funkyandroid.droidcon.uk.iosched.util.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static com.funkyandroid.droidcon.uk.iosched.provider.ScheduleDatabase.SessionsSpeakers;
import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.*;
import static com.funkyandroid.droidcon.uk.iosched.util.ParserUtils.sanitizeId;

public class SessionsHandler {

    private static final String TAG = makeLogTag(SessionsHandler.class);

    private static final String EVENT_TYPE_KEYNOTE = Sessions.SESSION_TYPE_KEYNOTE;
    private static final String EVENT_TYPE_OFFICE_HOURS = Sessions.SESSION_TYPE_OFFICE_HOURS;
    private static final String EVENT_TYPE_CODELAB = Sessions.SESSION_TYPE_CODELAB;
    private static final String EVENT_TYPE_SANDBOX = Sessions.SESSION_TYPE_SANDBOX;

    private static final int PARSE_FLAG_FORCE_SCHEDULE_REMOVE = 1;
    private static final int PARSE_FLAG_FORCE_SCHEDULE_ADD = 2;

    private Context mContext;

    public SessionsHandler(Context context) {
        mContext = context;
    }

    public ArrayList<ContentProviderOperation> fetchAndParse(
            ConferenceAPI conferenceAPI)
            throws IOException {
        // Set up the HTTP transport and JSON factory
        SessionsResponse sessions;
        SessionsResponse starredSessions = null;
        TracksResponse tracks;

        try {
            sessions = conferenceAPI.events().sessions().list(Config.EVENT_ID).setLimit(9999L).execute();
            tracks = conferenceAPI.events().tracks().list(Config.EVENT_ID).execute();

            if (sessions == null || sessions.getSessions() == null) {
                throw new HandlerException("Sessions list was null.");
            }
            if (tracks == null || tracks.getTracks() == null) {
                throw new HandlerException("trackDetails list was null.");
            }
        } catch (HandlerException e) {
            LOGE(TAG, "Fatal: error fetching sessions/tracks", e);
            return Lists.newArrayList();
        }

        final boolean profileAvailableBefore = PrefUtils.isDevsiteProfileAvailable(mContext);
        boolean profileAvailableNow = false;
/*
    TODO: Look at remote sync
        try {
            starredSessions = conferenceAPI.users().events().sessions().list(Config.EVENT_ID).execute();

            // If this succeeded, the user has a DevSite profile
            PrefUtils.markDevSiteProfileAvailable(mContext, true);
            profileAvailableNow = true;

        } catch (GoogleJsonResponseException e) {
            LOGE(TAG, "User does not have a developers.google.com profile. Not syncing remote " +
                    "personalized schedule.");
*/            starredSessions = null;
            PrefUtils.markDevSiteProfileAvailable(mContext, false);
//        }

/*
        TODO: Look at remote sync for starred events
        if (profileAvailableNow && !profileAvailableBefore) {
            LOGI(TAG, "developers.google.com mode change: DEVSITE_PROFILE_AVAILABLE=false -> true");
            // User's DevSite profile has come into existence. Re-upload tracks.
            ContentResolver cr = mContext.getContentResolver();
            String[] projection = new String[] {ScheduleContract.Sessions.SESSION_ID, Sessions.SESSION_TITLE};
            Cursor c = cr.query(ScheduleContract.BASE_CONTENT_URI.buildUpon().
                    appendPath("sessions").appendPath("starred").build(), projection, null, null, null);
            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String id = c.getString(0);
                    String title = c.getString(1);
                    LOGI(TAG, "Adding session: (" + id + ") " + title);
                    Uri sessionUri = ScheduleContract.Sessions.buildSessionUri(id);
                    SessionsHelper.uploadStarredSession(mContext, sessionUri, true);
                    c.moveToNext();
                }
            }
            // Hack: Use local starred sessions for now, to give the new sessions time to take effect
            // TODO(trevorjohns): Upload starred sessions should be synchronous to avoid this hack
            starredSessions = null;
        }
*/
        starredSessions = null;
        return buildContentProviderOperations(sessions, starredSessions, tracks);
    }

    public ArrayList<ContentProviderOperation> parseString(String sessionsJson, String tracksJson) {
        try {
            SessionsResponse sessions = new SessionsResponse();
            sessions.fromJSON(new JSONObject(sessionsJson));

            TracksResponse tracks = new TracksResponse();
            tracks.fromJSON(new JSONObject(tracksJson));
            return buildContentProviderOperations(sessions, null, tracks);
        } catch (JSONException e) {
            LOGE(TAG, "Error reading speakers from packaged data", e);
            return Lists.newArrayList();
        }
    }

    private ArrayList<ContentProviderOperation> buildContentProviderOperations(
            SessionsResponse sessions,
            SessionsResponse starredSessions,
            TracksResponse tracks) {

        // If there was no starred sessions response (e.g. there was an auth issue,
        // or this is a local sync), keep all the locally starred sessions.
        boolean retainLocallyStarredSessions = (starredSessions == null);

        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        // Build lookup table for starredSessions mappings
        HashSet<String> starredSessionsMap = new HashSet<String>();
        if (starredSessions != null) {
            List<SessionResponse> starredSessionList =
                    starredSessions.getSessions();
            if (starredSessionList != null) {
                for (SessionResponse session : starredSessionList) {
                    String sessionId = session.getId();
                    starredSessionsMap.add(sessionId);
                }
            }
        }

        // Build lookup table for track mappings
        // Assumes that sessions can only have one track. Not guarenteed by the Conference API,
        // but is being enforced by conference organizer policy.
        HashMap<String, TrackResponse> trackMap
                = new HashMap<String, TrackResponse>();
        if (tracks != null) {
            for (TrackResponse track : tracks.getTracks()) {
                List<String> sessionIds = track.getSessions();
                if (sessionIds != null) {
                    for (String sessionId : sessionIds) {
                        trackMap.put(sessionId, track);
                    }
                }
            }
        }

        if (sessions != null) {
            List<SessionResponse> sessionList = sessions.getSessions();
            int numSessions = sessionList.size();

            if (numSessions > 0) {
                LOGI(TAG, "Updating sessions data");

                Set<String> starredSessionIds = new HashSet<String>();
                if (retainLocallyStarredSessions) {
                    Cursor starredSessionsCursor = mContext.getContentResolver().query(
                            Sessions.CONTENT_STARRED_URI,
                            new String[]{ScheduleContract.Sessions.SESSION_ID},
                            null, null, null);
                    while (starredSessionsCursor.moveToNext()) {
                        starredSessionIds.add(starredSessionsCursor.getString(0));
                    }
                    starredSessionsCursor.close();
                }

                // Clear out existing sessions
                batch.add(ContentProviderOperation
                        .newDelete(ScheduleContract.addCallerIsSyncAdapterParameter(
                                Sessions.CONTENT_URI))
                        .build());

                // Maintain a list of created session block IDs
                Set<String> blockIds = new HashSet<String>();

                // Maintain a map of insert operations for sandbox-only blocks
                HashMap<String, ContentProviderOperation> sandboxBlocks = new HashMap<String, ContentProviderOperation>();


                for (SessionResponse session : sessionList) {
                    int flags = 0;
                    String sessionId = session.getId();
                    if (retainLocallyStarredSessions) {
                        flags = (starredSessionIds.contains(sessionId)
                                ? PARSE_FLAG_FORCE_SCHEDULE_ADD
                                : PARSE_FLAG_FORCE_SCHEDULE_REMOVE);
                    }

                    if (TextUtils.isEmpty(sessionId)) {
                        LOGW(TAG, "Found session with empty ID in API response.");
                        continue;
                    }

                    // Session title
                    String sessionTitle = session.getTitle();
                    String sessionSubtype = session.getSubtype();
                    if (EVENT_TYPE_CODELAB.equals(sessionSubtype)) {
                        sessionTitle = mContext.getString(
                                R.string.codelab_title_template, sessionTitle);
                    }

                    // Whether or not it's in the schedule
                    boolean inSchedule = starredSessionsMap.contains(sessionId);
                    if ((flags & PARSE_FLAG_FORCE_SCHEDULE_ADD) != 0
                            || (flags & PARSE_FLAG_FORCE_SCHEDULE_REMOVE) != 0) {
                        inSchedule = (flags & PARSE_FLAG_FORCE_SCHEDULE_ADD) != 0;
                    }

                    if (EVENT_TYPE_KEYNOTE.equals(sessionSubtype)) {
                        // Keynotes are always in your schedule.
                        inSchedule = true;
                    }

                    // Clean up session abstract
                    String sessionAbstract = session.getDescription();
                    if (sessionAbstract != null) {
                        sessionAbstract = sessionAbstract.replace('\r', '\n');
                    }

                    // Hashtags
                    TrackResponse track = trackMap.get(sessionId);
                    String hashtag = null;
                    if (track != null) {
                        hashtag = ParserUtils.sanitizeId(track.getTitle());
                    }

                    // Get block id
                    long sessionStartTime = session.getStartTimestamp().longValue() * 1000;
                    long sessionEndTime = session.getEndTimestamp().longValue() * 1000;
                    String blockId = ScheduleContract.Blocks.generateBlockId(
                            sessionStartTime, sessionEndTime);

                    if (!blockIds.contains(blockId) && !EVENT_TYPE_SANDBOX.equals(sessionSubtype)) {
                        // New non-sandbox block
                        if (sandboxBlocks.containsKey(blockId)) {
                            sandboxBlocks.remove(blockId);
                        }
                        String blockType;
                        String blockTitle;
                        if (EVENT_TYPE_KEYNOTE.equals(sessionSubtype)) {
                            blockType = ScheduleContract.Blocks.BLOCK_TYPE_KEYNOTE;
                            blockTitle = mContext.getString(R.string.schedule_block_title_keynote);
                        } else if (EVENT_TYPE_CODELAB.equals(sessionSubtype)) {
                            blockType = ScheduleContract.Blocks.BLOCK_TYPE_CODELAB;
                            blockTitle = mContext.getString(
                                    R.string.schedule_block_title_code_labs);
                        } else if (EVENT_TYPE_OFFICE_HOURS.equals(sessionSubtype)) {
                            blockType = ScheduleContract.Blocks.BLOCK_TYPE_OFFICE_HOURS;
                            blockTitle = mContext.getString(
                                    R.string.schedule_block_title_office_hours);
                        } else {
                            blockType = ScheduleContract.Blocks.BLOCK_TYPE_SESSION;
                            blockTitle = mContext.getString(
                                    R.string.schedule_block_title_sessions);
                        }

                        batch.add(ContentProviderOperation
                                .newInsert(ScheduleContract.Blocks.CONTENT_URI)
                                .withValue(ScheduleContract.Blocks.BLOCK_ID, blockId)
                                .withValue(ScheduleContract.Blocks.BLOCK_TYPE, blockType)
                                .withValue(ScheduleContract.Blocks.BLOCK_TITLE, blockTitle)
                                .withValue(ScheduleContract.Blocks.BLOCK_START, sessionStartTime)
                                .withValue(ScheduleContract.Blocks.BLOCK_END, sessionEndTime)
                                .build());
                        blockIds.add(blockId);

                    } else if (!sandboxBlocks.containsKey(blockId) && !blockIds.contains(blockId) && EVENT_TYPE_SANDBOX.equals(sessionSubtype)) {
                        // New sandbox-only block, add insert operation to map
                        String blockType = ScheduleContract.Blocks.BLOCK_TYPE_SANDBOX;
                        String blockTitle = mContext.getString(
                                R.string.schedule_block_title_sandbox);
                        sandboxBlocks.put(blockId,
                                ContentProviderOperation
                                        .newInsert(ScheduleContract.Blocks.CONTENT_URI)
                                        .withValue(ScheduleContract.Blocks.BLOCK_ID, blockId)
                                        .withValue(ScheduleContract.Blocks.BLOCK_TYPE, blockType)
                                        .withValue(ScheduleContract.Blocks.BLOCK_TITLE, blockTitle)
                                        .withValue(ScheduleContract.Blocks.BLOCK_START, sessionStartTime)
                                        .withValue(ScheduleContract.Blocks.BLOCK_END, sessionEndTime)
                                        .build());

                    }


                    // Insert session info
                    final ContentProviderOperation.Builder builder;
                    if (EVENT_TYPE_SANDBOX.equals(sessionSubtype)) {
                        // Sandbox companies go in the special sandbox table

                        builder = ContentProviderOperation
                                .newInsert(ScheduleContract
                                        .addCallerIsSyncAdapterParameter(ScheduleContract.Sandbox.CONTENT_URI))
                                .withValue(SyncColumns.UPDATED, System.currentTimeMillis())
                                .withValue(ScheduleContract.Sandbox.COMPANY_ID, sessionId)
                                .withValue(ScheduleContract.Sandbox.COMPANY_NAME, sessionTitle)
                                .withValue(ScheduleContract.Sandbox.COMPANY_DESC, sessionAbstract)
                                .withValue(ScheduleContract.Sandbox.COMPANY_URL, session.getWebLink())
                                .withValue(ScheduleContract.Sandbox.COMPANY_LOGO_URL, session.getIconUrl())
                                .withValue(ScheduleContract.Sandbox.ROOM_ID, sanitizeId(session.getLocation()))
                                .withValue(ScheduleContract.Sandbox.TRACK_ID, (track != null ? track.getId() : null))
                                .withValue(ScheduleContract.Sandbox.BLOCK_ID, blockId);
                        batch.add(builder.build());

                    } else {
                        // All other fields go in the normal sessions table
                        builder = ContentProviderOperation
                                .newInsert(ScheduleContract
                                        .addCallerIsSyncAdapterParameter(Sessions.CONTENT_URI))
                                .withValue(SyncColumns.UPDATED, System.currentTimeMillis())
                                .withValue(Sessions.SESSION_ID, sessionId)
                                .withValue(Sessions.SESSION_TYPE, sessionSubtype)
                                .withValue(Sessions.SESSION_LEVEL, null)            // Not available
                                .withValue(Sessions.SESSION_TITLE, sessionTitle)
                                .withValue(Sessions.SESSION_ABSTRACT, sessionAbstract)
                                .withValue(Sessions.SESSION_HASHTAGS, hashtag)
                                .withValue(Sessions.SESSION_TAGS, null)             // Not available
                                .withValue(Sessions.SESSION_URL, session.getWebLink())
                                .withValue(Sessions.SESSION_MODERATOR_URL, null)    // Not available
                                .withValue(Sessions.SESSION_REQUIREMENTS, null)     // Not available
                                .withValue(Sessions.SESSION_STARRED, inSchedule)
                                .withValue(Sessions.SESSION_YOUTUBE_URL, null)      // Not available
                                .withValue(Sessions.SESSION_PDF_URL, null)          // Not available
                                .withValue(Sessions.SESSION_NOTES_URL, null)        // Not available
                                .withValue(Sessions.ROOM_ID, sanitizeId(session.getLocation()))
                                .withValue(Sessions.BLOCK_ID, blockId);

                        batch.add(builder.build());
                    }

                    // Replace all session speakers
                    final Uri sessionSpeakersUri = Sessions.buildSpeakersDirUri(sessionId);
                    batch.add(ContentProviderOperation
                            .newDelete(ScheduleContract
                                    .addCallerIsSyncAdapterParameter(sessionSpeakersUri))
                            .build());

                    List<String> presenterIds = session.getPresenterIds();
                    if (presenterIds != null) {
                        for (String presenterId : presenterIds) {
                            batch.add(ContentProviderOperation.newInsert(sessionSpeakersUri)
                                    .withValue(SessionsSpeakers.SESSION_ID, sessionId)
                                    .withValue(SessionsSpeakers.SPEAKER_ID, presenterId).build());
                        }
                    }

                    // Add track mapping
                    if (track != null) {
                        String trackId = track.getId();
                        if (trackId != null) {
                            final Uri sessionTracksUri = ScheduleContract.addCallerIsSyncAdapterParameter(
                                    ScheduleContract.Sessions.buildTracksDirUri(sessionId));
                            batch.add(ContentProviderOperation.newInsert(sessionTracksUri)
                                    .withValue(ScheduleDatabase.SessionsTracks.SESSION_ID, sessionId)
                                    .withValue(ScheduleDatabase.SessionsTracks.TRACK_ID, trackId).build());
                        }
                    }

                    // Codelabs: Add mapping to codelab table
                    if (EVENT_TYPE_CODELAB.equals(sessionSubtype)) {
                        final Uri sessionTracksUri = ScheduleContract.addCallerIsSyncAdapterParameter(
                                ScheduleContract.Sessions.buildTracksDirUri(sessionId));
                        batch.add(ContentProviderOperation.newInsert(sessionTracksUri)
                                .withValue(ScheduleDatabase.SessionsTracks.SESSION_ID, sessionId)
                                .withValue(ScheduleDatabase.SessionsTracks.TRACK_ID, "CODE_LABS").build());
                    }
                }

                // Insert sandbox-only blocks
                batch.addAll(sandboxBlocks.values());
            }
        }

        return batch;
    }

}
