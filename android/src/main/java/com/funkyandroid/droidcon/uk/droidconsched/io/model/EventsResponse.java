package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * EventsResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class EventsResponse extends ServerResponse {

    private List<EventResponse> events;

    public EventsResponse()
    {
    }

    public List<EventResponse> getEvents()
    {
        return this.events;
    }

    public EventsResponse setEvents(List<EventResponse> events)
    {
        this.events = events;
        return this;
    }
}