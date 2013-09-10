package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * TodoResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class TodoResponse extends ServerResponse {

    private List<String> allowedAttendeeTypes;

    private Double completionTimestamp;

    private String description;

    private Double endTimestamp;

    private String eventId;

    private String id;

    private Double startTimestamp;

    public TodoResponse()
    {
    }

    public List<String> getAllowedAttendeeTypes()
    {
        return this.allowedAttendeeTypes;
    }

    public TodoResponse setAllowedAttendeeTypes(List<String> allowedAttendeeTypes)
    {
        this.allowedAttendeeTypes = allowedAttendeeTypes;
        return this;
    }

    public Double getCompletionTimestamp()
    {
        return this.completionTimestamp;
    }

    public TodoResponse setCompletionTimestamp(Double completionTimestamp)
    {
        this.completionTimestamp = completionTimestamp;
        return this;
    }

    public String getDescription()
    {
        return this.description;
    }

    public TodoResponse setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public Double getEndTimestamp()
    {
        return this.endTimestamp;
    }

    public TodoResponse setEndTimestamp(Double endTimestamp)
    {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public TodoResponse setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public TodoResponse setId(String id)
    {
        this.id = id;
        return this;
    }

    public Double getStartTimestamp()
    {
        return this.startTimestamp;
    }

    public TodoResponse setStartTimestamp(Double startTimestamp)
    {
        this.startTimestamp = startTimestamp;
        return this;
    }
}
