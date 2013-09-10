package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

/**
 * PresenterResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class PresenterResponse extends ServerResponse {
    private String bio;

    private String id;

    private String name;

    private String plusoneUrl;

    private String publicPlusId;

    private String thumbnailUrl;

    public PresenterResponse()
    {
    }

    public String getBio()
    {
        return this.bio;
    }

    public PresenterResponse setBio(String bio)
    {
        this.bio = bio;
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public PresenterResponse setId(String id)
    {
        this.id = id;
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public PresenterResponse setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getPlusoneUrl()
    {
        return this.plusoneUrl;
    }

    public PresenterResponse setPlusoneUrl(String plusoneUrl)
    {
        this.plusoneUrl = plusoneUrl;
        return this;
    }

    public String getPublicPlusId()
    {
        return this.publicPlusId;
    }

    public PresenterResponse setPublicPlusId(String publicPlusId)
    {
        this.publicPlusId = publicPlusId;
        return this;
    }

    public String getThumbnailUrl()
    {
        return this.thumbnailUrl;
    }

    public PresenterResponse setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }
}