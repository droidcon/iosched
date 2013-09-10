package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.events.Announcements;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.events.Sessions;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.events.Presenters;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.events.Tracks;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

/**
 * Events as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Events
{
    public Events()
    {
    }

    public Get get(String eventId)
            throws IOException
    {
        Get result = new Get(eventId);
        return result;
    }

    public List list()
            throws IOException
    {
        List result = new List();
        return result;
    }

    public Announcements announcements()
    {
        return new Announcements();
    }

    public Presenters presenters()
    {
        return new Presenters();
    }

    public Sessions sessions()
    {
        return new Sessions();
    }

    public Tracks tracks()
    {
        return new Tracks();
    }


    public class List extends ServerRequest<EventsResponse>
    {
        private static final String REST_PATH = "events";

        private Long limit;

        private Long searchWindowDays;

        private Double startTimestamp;

        protected List()
        {
            super("GET", "events", null, EventsResponse.class);
        }

        public Long getLimit()
        {
            return this.limit;
        }

        public List setLimit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Long getSearchWindowDays()
        {
            return this.searchWindowDays;
        }

        public List setSearchWindowDays(Long searchWindowDays) {
            this.searchWindowDays = searchWindowDays;
            return this;
        }

        public Double getStartTimestamp()
        {
            return this.startTimestamp;
        }

        public List setStartTimestamp(Double startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }
    }

    public class Get extends ServerRequest<EventResponse>
    {
        private static final String REST_PATH = "events/{eventId}";

        private String eventId;

        protected Get(String eventId)
        {
            super("GET", "events/{eventId}", null, EventResponse.class);
            this.eventId = ((String) Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified."));
        }


        public String getEventId()
        {
            return this.eventId;
        }

        public Get setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
    }
}
