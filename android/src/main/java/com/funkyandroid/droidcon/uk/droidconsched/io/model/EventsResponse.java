package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerArrayResponse;

import java.util.List;

/**
 * EventsResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class EventsResponse extends ServerArrayResponse<EventResponse> {

    public List<EventResponse> getEvents()
    {
        return getArrayContents();
    }

    @Override
    protected String getArrayAttributeName() {
        return "events";
    }

    @Override
    protected EventResponse getNewObject() {
        return new EventResponse();
    }
}