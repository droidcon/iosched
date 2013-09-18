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

import android.content.*;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import com.funkyandroid.droidcon.uk.droidconsched.io.ConferenceAPI;
import com.funkyandroid.droidcon.uk.iosched.Config;
import com.funkyandroid.droidcon.uk.iosched.R;
import com.funkyandroid.droidcon.uk.iosched.io.*;
import com.funkyandroid.droidcon.uk.iosched.io.map.model.Tile;
import com.funkyandroid.droidcon.uk.iosched.provider.ScheduleContract;
import com.funkyandroid.droidcon.uk.iosched.util.Lists;
import com.larvalabs.svgandroid.SVGParseException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.*;

/**
 * A helper class for dealing with sync and other remote persistence operations.
 * All operations occur on the thread they're called from, so it's best to wrap
 * calls in an {@link android.os.AsyncTask}, or better yet, a
 * {@link android.app.Service}.
 */
public class SyncHelper {
    private static final String TAG = makeLogTag(SyncHelper.class);

    public static final int FLAG_SYNC_LOCAL = 0x1;
    public static final int FLAG_SYNC_REMOTE = 0x2;

    private static final int LOCAL_VERSION_CURRENT = 27;
    private static final String LOCAL_MAPVERSION_CURRENT = "\"vlh7Ig\"";

    private Context mContext;

    public SyncHelper(Context context) {
        mContext = context;
    }

    /**
     * Start a sync manually using the supplied context.
     *
     * @param context The context to start the sync service under.
     */
    public static void requestManualSync(Context context) {
        Context appContext = context.getApplicationContext();
        Intent serviceIntent = new Intent(appContext, SyncService.class);
        serviceIntent.putExtra(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        appContext.startService(serviceIntent);
    }

    /**
     * Loads conference information (sessions, rooms, tracks, speakers, etc.)
     * from a local static cache data and then syncs down data from the
     * Conference API.
     *
     * @param syncResult Optional {@link SyncResult} object to populate.
     * @throws IOException
     */
    public void performSync(SyncResult syncResult, int flags) throws IOException {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final int localVersion = prefs.getInt("local_data_version", 0);

        // Bulk of sync work, performed by executing several fetches from
        // local and online sources.
        final ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        LOGI(TAG, "Performing sync");

        if ((flags & FLAG_SYNC_LOCAL) != 0) {
            final long startLocal = System.currentTimeMillis();
            final boolean localParse = localVersion < LOCAL_VERSION_CURRENT;
            LOGD(TAG, "found localVersion=" + localVersion + " and LOCAL_VERSION_CURRENT="
                    + LOCAL_VERSION_CURRENT);
            // Only run local sync if there's a newer version of data available
            // than what was last locally-sync'd.
            if (localParse) {
                // Load static local data
                LOGI(TAG, "Local syncing rooms");
                batch.addAll(new RoomsHandler(mContext).parse(
                        JSONHandler.parseResource(mContext, R.raw.rooms)));
                LOGI(TAG, "Local syncing blocks");
                batch.addAll(new BlocksHandler(mContext).parse(
                        JSONHandler.parseResource(mContext, R.raw.common_slots)));
                LOGI(TAG, "Local syncing tracks");
                batch.addAll(new TracksHandler(mContext).parse(
                        JSONHandler.parseResource(mContext, R.raw.tracks)));
                LOGI(TAG, "Local syncing speakers");
                batch.addAll(new SpeakersHandler(mContext).parseString(
                        JSONHandler.parseResource(mContext, R.raw.speakers)));
                LOGI(TAG, "Local syncing sessions");
                batch.addAll(new SessionsHandler(mContext).parseString(
                        JSONHandler.parseResource(mContext, R.raw.sessions),
                        JSONHandler.parseResource(mContext, R.raw.session_tracks)));
                LOGI(TAG, "Local syncing search suggestions");
                batch.addAll(new SearchSuggestHandler(mContext).parse(
                        JSONHandler.parseResource(mContext, R.raw.search_suggest)));
                LOGI(TAG, "Local syncing map");
                MapPropertyHandler mapHandler = new MapPropertyHandler(mContext);
                batch.addAll(mapHandler.parse(
                        JSONHandler.parseResource(mContext, R.raw.map)));
                //need to sync tile files before data is updated in content provider
// We're not using the map                syncMapTiles(mapHandler.getTiles());

                prefs.edit().putInt("local_data_version", LOCAL_VERSION_CURRENT).commit();
                prefs.edit().putString("local_mapdata_version", LOCAL_MAPVERSION_CURRENT).commit();
                if (syncResult != null) {
                    ++syncResult.stats.numUpdates; // TODO: better way of indicating progress?
                    ++syncResult.stats.numEntries;
                }
            }

            LOGD(TAG, "Local sync took " + (System.currentTimeMillis() - startLocal) + "ms");

            try {
                // Apply all queued up batch operations for local data.
                resolver.applyBatch(ScheduleContract.CONTENT_AUTHORITY, batch);
            } catch (RemoteException e) {
                throw new RuntimeException("Problem applying batch operation", e);
            } catch (OperationApplicationException e) {
                throw new RuntimeException("Problem applying batch operation", e);
            }

            batch = new ArrayList<ContentProviderOperation>();
        }

        if ((flags & FLAG_SYNC_REMOTE) != 0 && isOnline()) {
            ConferenceAPI conferenceAPI = new ConferenceAPI();
            final long startRemote = System.currentTimeMillis();
            LOGI(TAG, "Remote syncing announcements");
            batch.addAll(new AnnouncementsFetcher(mContext).fetchAndParse());
            LOGI(TAG, "Remote syncing speakers");
            batch.addAll(new SpeakersHandler(mContext).fetchAndParse(conferenceAPI));
            LOGI(TAG, "Remote syncing sessions");
            batch.addAll(new SessionsHandler(mContext).fetchAndParse(conferenceAPI));
            // Map sync
            batch.addAll(remoteSyncMapData(Config.GET_MAP_URL,prefs));
            LOGD(TAG, "Remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
            if (syncResult != null) {
                ++syncResult.stats.numUpdates; // TODO: better way of indicating progress?
                ++syncResult.stats.numEntries;
            }

            // Sync feedback stuff
            LOGI(TAG, "Syncing session feedback");
            batch.addAll(new FeedbackHandler(mContext).uploadNew(conferenceAPI));

            // all other IOExceptions are thrown
            LOGI(TAG, "Sync complete");
        }

        try {
            // Apply all queued up remaining batch operations (only remote content at this point).
            resolver.applyBatch(ScheduleContract.CONTENT_AUTHORITY, batch);

            // Update search index
            resolver.update(ScheduleContract.SearchIndex.CONTENT_URI, new ContentValues(),
                    null, null);

            // Delete empty blocks
            Cursor emptyBlocksCursor = resolver.query(ScheduleContract.Blocks.CONTENT_URI,
                    new String[]{ScheduleContract.Blocks.BLOCK_ID,ScheduleContract.Blocks.SESSIONS_COUNT},
                    ScheduleContract.Blocks.EMPTY_SESSIONS_SELECTION, null, null);
            batch = new ArrayList<ContentProviderOperation>();
            int numDeletedEmptyBlocks = 0;
            while (emptyBlocksCursor.moveToNext()) {
                batch.add(ContentProviderOperation
                        .newDelete(ScheduleContract.Blocks.buildBlockUri(
                                emptyBlocksCursor.getString(0)))
                        .build());
                ++numDeletedEmptyBlocks;
            }
            emptyBlocksCursor.close();
            resolver.applyBatch(ScheduleContract.CONTENT_AUTHORITY, batch);
            LOGD(TAG, "Deleted " + numDeletedEmptyBlocks + " empty session blocks.");
        } catch (RemoteException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        }
    }

    public void addOrRemoveSessionFromSchedule(Context context, String sessionId,
            boolean inSchedule) throws IOException {
        LOGI(TAG, "Updating session on user schedule: " + sessionId);

/*
        TODO: Look at non-Google Developers way of doing this.
        Googledevelopers conferenceAPI = getConferenceAPIClient();
        try {
            sendScheduleUpdate(conferenceAPI, context, sessionId, inSchedule);
        } catch (GoogleJsonResponseException e) {
            LOGI(TAG, "Schedule change failed", e);
        }
*/
    }

/*
    TODO: Look at non-Google Developers way of doing this.
    private void sendScheduleUpdate(Googledevelopers conferenceAPI,
            Context context, String sessionId, boolean inSchedule) throws IOException {
        if (inSchedule) {
            conferenceAPI.users().events().sessions().update(Config.EVENT_ID, sessionId, null).execute();
        } else {
            conferenceAPI.users().events().sessions().delete(Config.EVENT_ID, sessionId).execute();
        }
    }
*/

    private ArrayList<ContentProviderOperation> remoteSyncMapData(String urlString,
            SharedPreferences preferences) throws IOException {
        final String localVersion = preferences.getString("local_mapdata_version", null);

        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

/* TODO : Look at remote sync for map data

        BasicHttpClient httpClient = new BasicHttpClient();
        httpClient.setRequestLogger(mQuietLogger);
        httpClient.addHeader("If-None-Match", localVersion);

        LOGD(TAG,"Local map version: "+localVersion);
        HttpResponse response = httpClient.get(urlString, null);
        final int status = response.getStatus();

        if (status == HttpURLConnection.HTTP_OK) {
            // Data has been updated, otherwise would have received HTTP_NOT_MODIFIED
            LOGI(TAG, "Remote syncing map data");
            final List<String> etag = response.getHeaders().get("ETag");
            if (etag != null && etag.size() > 0) {
                MapPropertyHandler handler = new MapPropertyHandler(mContext);
                batch.addAll(handler.parse(response.getBodyAsString()));
                syncMapTiles(handler.getTiles());

                // save new etag as version
                preferences.edit().putString("local_mapdata_version", etag.get(0)).commit();
            }
        } //else: no update
*/
        return batch;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Synchronise the map overlay files either from the local assets (if available) or from a remote url.
     *
     * @param collection Set of tiles containing a local filename and remote url.
     * @throws IOException
     */
    private void syncMapTiles(Collection<Tile> collection) throws IOException, SVGParseException {

        //keep track of used files, unused files are removed
        ArrayList<String> usedTiles = Lists.newArrayList();

/* TODO : Look at remote sync for map data

        for(Tile tile : collection){
            final String filename = tile.filename;
            final String url = tile.url;

            usedTiles.add(filename);

            if (!MapUtils.hasTile(mContext, filename)) {
                // copy or download the tile if it is not stored yet
                if (MapUtils.hasTileAsset(mContext, filename)) {
                    // file already exists as an asset, copy it
                    MapUtils.copyTileAsset(mContext, filename);
                } else {
                    // download the file
                    File tileFile = MapUtils.getTileFile(mContext, filename);
                    BasicHttpClient httpClient = new BasicHttpClient();
                    httpClient.setRequestLogger(mQuietLogger);
                    HttpResponse httpResponse = httpClient.get(url, null);
                    writeFile(httpResponse.getBody(), tileFile);

                    // ensure the file is valid SVG
                    InputStream is = new FileInputStream(tileFile);
                    SVG svg = SVGParser.getSVGFromInputStream(is);
                    is.close();
                }
            }
        }

        MapUtils.removeUnusedTiles(mContext, usedTiles);
*/
    }

    /**
     * Write the byte array directly to a file.
     * @throws IOException
     */
    private void writeFile(byte[] data, File file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(data);
        bos.close();
    }
}
