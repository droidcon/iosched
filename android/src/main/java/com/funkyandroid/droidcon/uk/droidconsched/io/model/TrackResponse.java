package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * TrackResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class TrackResponse extends ServerResponse {

    private String description;

    private String iconUrl;

    private String id;

    private List<String> sessions;

    private String title;

    public TrackResponse()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public TrackResponse setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public TrackResponse setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public TrackResponse setId(String id)
    {
        this.id = id;
        return this;
    }

    public List<String> getSessions()
    {
        return this.sessions;
    }

    public TrackResponse setSessions(List<String> sessions)
    {
        this.sessions = sessions;
        return this;
    }

    public String getTitle()
    {
        return this.title;
    }

    public TrackResponse setTitle(String title)
    {
        this.title = title;
        return this;
    }
}