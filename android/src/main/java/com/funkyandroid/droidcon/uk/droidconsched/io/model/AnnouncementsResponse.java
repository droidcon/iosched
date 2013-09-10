package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * AnnouncementsResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class AnnouncementsResponse extends ServerResponse {

    private List<AnnouncementResponse> announcements;

    public AnnouncementsResponse()
    {
    }

    public List<AnnouncementResponse> getAnnouncements()
    {
        return this.announcements;
    }

    public AnnouncementsResponse setAnnouncements(List<AnnouncementResponse> announcements)
    {
        this.announcements = announcements;
        return this;
    }
}