package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

/**
 * Class representing an individual tweet.
 */
public class TweetResponse extends ServerResponse {

    // Tweet details
    private String id;

    private String text;

    private String url = null;

    private Long retweetCount;

    private Long createdAt;


    // Tweet creator information
    private String name;

    private String screenName;

    private Long userId;

    private Boolean verified;

    private String profileImageURL;


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

    public Long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
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
