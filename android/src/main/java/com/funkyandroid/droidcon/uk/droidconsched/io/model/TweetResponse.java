package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

/**
 * Class representing an individual tweet.
 */
public class TweetResponse extends ServerResponse {

    private String id;

    private String name;

    private String text;

    private String url = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Create a URL for people to view the details of a tweet
     */

    public String getUrl() {
        synchronized (this) {
            if(url == null ) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://twitter.com/");
                urlBuilder.append(getName());
                urlBuilder.append("/status/");
                urlBuilder.append(getId());
                url = urlBuilder.toString();
            }
        }
        return url;
    }
}
