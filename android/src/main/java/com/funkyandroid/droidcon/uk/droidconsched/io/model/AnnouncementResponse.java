package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * AnnouncementResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class AnnouncementResponse extends ServerResponse {

    private String link;

    private String summary;

    private Double timestamp;

    private String title;

    private List<String> tracks;

    public AnnouncementResponse()
    {
    }

    public String getLink()
    {
        return this.link;
    }

    public AnnouncementResponse setLink(String link)
    {
        this.link = link;
        return this;
    }

    public String getSummary()
    {
        return this.summary;
    }

    public AnnouncementResponse setSummary(String summary)
    {
        this.summary = summary;
        return this;
    }

    public Double getTimestamp()
    {
        return this.timestamp;
    }

    public AnnouncementResponse setTimestamp(Double timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    public String getTitle()
    {
        return this.title;
    }

    public AnnouncementResponse setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public List<String> getTracks()
    {
        return this.tracks;
    }

    public AnnouncementResponse setTracks(List<String> tracks)
    {
        this.tracks = tracks;
        return this;
    }
}
