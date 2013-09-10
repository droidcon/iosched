package com.funkyandroid.droidcon.uk.droidconsched.io.model.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.*;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.events.sessions.Feedback;
import com.google.api.client.util.Preconditions;

import java.io.IOException;

/**
 * Events.Sessions as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Sessions
{
    public Sessions()
    {
    }

    public Feedback feedback(String eventId, String sessionId, ModifyFeedbackRequest content)
            throws IOException
    {
        Feedback result = new Feedback(eventId, sessionId, content);
        return result;
    }

    public Get get(String eventId, String sessionId)
            throws IOException
    {
        Get result = new Get(eventId, sessionId);
        return result;
    }

    public List list(String eventId)
            throws IOException
    {
        List result = new List(eventId);
        return result;
    }

    public Presenters presenters()
    {
        return new Presenters();
    }


    public class List extends ServerRequest<SessionsResponse>
    {
        private static final String REST_PATH = "events/{eventId}/sessions";

        private String eventId;

        private Long limit;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/sessions", null, SessionsResponse.class);
            this.eventId = ((String)Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified."));
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public List setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Long getLimit()
        {
            return this.limit;
        }

        public List setLimit(Long limit) {
            this.limit = limit;
            return this;
        }
    }

    public class Get extends ServerRequest<SessionResponse>
    {
        private static final String REST_PATH = "events/{eventId}/sessions/{sessionId}";

        private String eventId;

        private String sessionId;

        protected Get(String eventId, String sessionId)
        {
            super("GET", "events/{eventId}/sessions/{sessionId}", null, SessionResponse.class);
            this.eventId = ((String)Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified."));
            this.sessionId = ((String)Preconditions.checkNotNull(sessionId, "Required parameter sessionId must be specified."));
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Get setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getSessionId()
        {
            return this.sessionId;
        }

        public Get setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
    }
}
