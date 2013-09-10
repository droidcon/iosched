package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * EventResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class EventResponse extends ServerResponse {

    private String description;

    private Double endTimestamp;

    private String iconUrl;

    private String id;

    private Boolean isLivestream;

    private String location;

    private List<String> presenterIds;

    private Double startTimestamp;

    private String subtype;

    private String title;

    private String youtubeId;

    private String youtubeUrl;

    public EventResponse()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public EventResponse setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public Double getEndTimestamp()
    {
        return this.endTimestamp;
    }

    public EventResponse setEndTimestamp(Double endTimestamp)
    {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public EventResponse setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public EventResponse setId(String id)
    {
        this.id = id;
        return this;
    }

    public Boolean getIsLivestream()
    {
        return this.isLivestream;
    }

    public EventResponse setIsLivestream(Boolean isLivestream)
    {
        this.isLivestream = isLivestream;
        return this;
    }

    public String getLocation()
    {
        return this.location;
    }

    public EventResponse setLocation(String location)
    {
        this.location = location;
        return this;
    }

    public List<String> getPresenterIds()
    {
        return this.presenterIds;
    }

    public EventResponse setPresenterIds(List<String> presenterIds)
    {
        this.presenterIds = presenterIds;
        return this;
    }

    public Double getStartTimestamp()
    {
        return this.startTimestamp;
    }

    public EventResponse setStartTimestamp(Double startTimestamp)
    {
        this.startTimestamp = startTimestamp;
        return this;
    }

    public String getSubtype()
    {
        return this.subtype;
    }

    public EventResponse setSubtype(String subtype)
    {
        this.subtype = subtype;
        return this;
    }

    public String getTitle()
    {
        return this.title;
    }

    public EventResponse setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String getYoutubeId()
    {
        return this.youtubeId;
    }

    public EventResponse setYoutubeId(String youtubeId)
    {
        this.youtubeId = youtubeId;
        return this;
    }

    public String getYoutubeUrl()
    {
        return this.youtubeUrl;
    }

    public EventResponse setYoutubeUrl(String youtubeUrl)
    {
        this.youtubeUrl = youtubeUrl;
        return this;
    }
}