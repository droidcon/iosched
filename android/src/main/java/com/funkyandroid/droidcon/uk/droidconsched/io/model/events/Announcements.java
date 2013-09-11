package com.funkyandroid.droidcon.uk.droidconsched.io.model.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.AnnouncementsResponse;

import java.io.IOException;

/**
 * Events.Sessions as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Announcements
{
    public Announcements()
    {
    }

    public List list(String eventId)
            throws IOException
    {
        List result = new List(eventId);
        return result;
    }

    public class List extends ServerRequest<AnnouncementsResponse>
    {
        private static final String REST_PATH = "events/{eventId}/announcements";

        private String eventId;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/announcements", null, AnnouncementsResponse.class);
            assert eventId != null;
            this.eventId = eventId;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public List setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
    }
}
