package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerArrayResponse;

import java.util.List;

/**
 * AnnouncementsResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class AnnouncementsResponse extends ServerArrayResponse<AnnouncementResponse> {

    public List<AnnouncementResponse> getAnnouncements()
    {
        return getArrayContents();
    }

    @Override
    protected String getArrayAttributeName() {
        return "events";
    }

    @Override
    protected AnnouncementResponse getNewObject() {
        return new AnnouncementResponse();
    }
}