package com.funkyandroid.droidcon.uk.droidconsched.io.model.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TrackResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TracksResponse;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

/**
 * Events.Tracks as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Tracks
{
    public Tracks()
    {
    }

    public Get get(String eventId, String trackId)
            throws IOException
    {
        Get result = new Get(eventId, trackId);
        return result;
    }

    public List list(String eventId)
            throws IOException
    {
        List result = new List(eventId);
        return result;
    }

    public class List extends ServerRequest<TracksResponse>
    {
        private static final String REST_PATH = "events/{eventId}/tracks";

        private String eventId;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/tracks", null, TracksResponse.class);
            this.eventId = ((String) Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified."));
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

    public class Get extends ServerRequest<TrackResponse>
    {
        private static final String REST_PATH = "events/{eventId}/tracks/{trackId}";

        private String eventId;

        private String trackId;

        protected Get(String eventId, String trackId)
        {
            super("GET", "events/{eventId}/tracks/{trackId}", null, TrackResponse.class);
            this.eventId = ((String)Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified."));
            this.trackId = ((String)Preconditions.checkNotNull(trackId, "Required parameter trackId must be specified."));
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Get setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getTrackId()
        {
            return this.trackId;
        }

        public Get setTrackId(String trackId) {
            this.trackId = trackId;
            return this;
        }
    }
}
