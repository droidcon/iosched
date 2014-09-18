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

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

    public Long createdAt;
    public String name;
    public Boolean verified;
    public String text;
    public String id;
    public String screenName;
    public Long userId;
    public String profileImageURL;
    public Long retweetCount;

    private String url;

    public Tweet(JSONObject jsonObject) throws JSONException {
        createdAt = jsonObject.getLong("createdAt");
        name = jsonObject.getString("name");
        verified = jsonObject.getBoolean("verified");
        text = jsonObject.getString("text");
        id = jsonObject.getString("id");
        screenName = jsonObject.getString("screenName");
        userId = jsonObject.getLong("userId");
        profileImageURL = jsonObject.getString("profileImageURL");
        retweetCount = jsonObject.getLong("retweetCount");
    }

    public String getUrlForTweet() {
        synchronized (this) {
            if(url == null ) {
                url = "https://twitter.com/" + name + "/status/" + id;
            }
        }
        return url;
    }
}
