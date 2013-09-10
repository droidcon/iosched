package com.funkyandroid.droidcon.uk.droidconsched.io.model.events.sessions;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.PresentersResponse;

import java.io.IOException;

/**
 * Events.Sessions.Presenters as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Presenters
{
    public Presenters()
    {
    }

    public List list(String eventId, String sessionId)
            throws IOException
    {
        List result = new List(eventId, sessionId);
        return result;
    }

    public class List extends ServerRequest<PresentersResponse>
    {
        private static final String REST_PATH = "events/{eventId}/sessions/{sessionId}/presenters";

        private String eventId;

        private String sessionId;

        protected List(String eventId, String sessionId)
        {
            super("GET", "events/{eventId}/sessions/{sessionId}/presenters", null, PresentersResponse.class);
            assert eventId != null;
            this.eventId = eventId;

            assert sessionId != null;
            this.sessionId = sessionId;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public List setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getSessionId()
        {
            return this.sessionId;
        }

        public List setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
    }
}
