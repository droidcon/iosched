/*
 * Copyright 2014 Funky Android Ltd. All rights reserved.
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

package com.google.samples.apps.iosched.social.twitter;

import com.google.samples.apps.iosched.BuildConfig;
import com.google.samples.apps.iosched.util.ServerDataFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.samples.apps.iosched.util.LogUtils.LOGD;

public class TweetFetcher extends ServerDataFetcher {

    private static final String TAG = "TweetFetcher";

    private static List<Tweet> lastFetchResults;
    private static long lastFetchTime = Long.MIN_VALUE;
    private static String dataTimestamp = null;

    private static final long MINIMUM_REFRESH_INTERVAL = 5*60*1000; // 5 mins


    public List<Tweet> fetchTweets() throws IOException {
        synchronized (TweetFetcher.class) {
            if (lastFetchResults != null
                    && lastFetchTime + MINIMUM_REFRESH_INTERVAL > System.currentTimeMillis()) {
                return lastFetchResults;
            }

            try {
                String tweetData = fetchDataIfNewer(TAG, getTweetsUrl(), dataTimestamp);
                if (tweetData == null) {
                    LOGD(TAG, "No new tweet data is available");
                    return lastFetchResults;
                }

                List<Tweet> tweets = parseTweets(tweetData);
                if (tweets == null || tweets.isEmpty()) {
                    return null;
                }

                lastFetchResults = tweets;
                lastFetchTime = System.currentTimeMillis();
                dataTimestamp = getServerDataTimestamp();
                return lastFetchResults;
            } catch(JSONException e) {
                throw new IOException(e);
            }
        }
    }

    private String getTweetsUrl() {
        String manifestUrl = BuildConfig.CONFERENCE_DATA_MANIFEST_URL;
        int i = manifestUrl.lastIndexOf('/');
        return manifestUrl.substring(0, i) + "/tweets.json";
    }

    private List<Tweet> parseTweets(final String tweetsJSON)
            throws JSONException {
        JSONObject jsonObject = new JSONObject(tweetsJSON);
        JSONArray tweetArray = jsonObject.getJSONArray("tweets");

        List<Tweet> tweets = new ArrayList<Tweet>();
        for(int i = 0 ; i < tweetArray.length() ; i++) {
            JSONObject thisTweet = tweetArray.getJSONObject(i);
            tweets.add(new Tweet(thisTweet));
        }

        return tweets;
    }

    private static final class InstanceHolder {
        static final TweetFetcher INSTANCE = new TweetFetcher();
    }

    public static TweetFetcher getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
