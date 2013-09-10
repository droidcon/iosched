package com.funkyandroid.droidcon.uk.droidconsched.io.model.users.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.ModifyUserSessionRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.SessionsResponse;

import java.io.IOException;

/**
 * Users.Events.Sessions as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Sessions
{
    public Sessions()
    {
    }

    public Delete delete(String eventId, String sessionId)
            throws IOException
    {
        return new Delete(eventId, sessionId);
    }

    public List list(String eventId)
            throws IOException
    {
        return new List(eventId);
    }

    public Update update(String eventId, String sessionId, ModifyUserSessionRequest content)
            throws IOException
    {
        return new Update(eventId, sessionId, content);
    }

    public class Update extends ServerRequest<SessionsResponse>
    {
        private static final String REST_PATH = "userevents/{eventId}/sessions/{sessionId}";

        private String eventId;

        private String sessionId;

        protected Update(String eventId, String sessionId, ModifyUserSessionRequest content)
        {
            super("PUT", "userevents/{eventId}/sessions/{sessionId}", content, SessionsResponse.class);

            assert eventId != null;
            this.eventId = eventId;

            assert sessionId != null;
            this.sessionId = sessionId;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Update setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getSessionId()
        {
            return this.sessionId;
        }

        public Update setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
    }

    public class List extends ServerRequest<SessionsResponse>
    {
        private static final String REST_PATH = "userevents/{eventId}/sessions";

        private String eventId;

        protected List(String eventId)
        {
            super("GET", "userevents/{eventId}/sessions", null, SessionsResponse.class);
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

    public class Delete extends ServerRequest<SessionsResponse>
    {
        private static final String REST_PATH = "userevents/{eventId}/sessions/{sessionId}";

        private String eventId;

        private String sessionId;

        protected Delete(String eventId, String sessionId)
        {
            super("DELETE", "userevents/{eventId}/sessions/{sessionId}", null, SessionsResponse.class);
            assert eventId != null;
            this.eventId = eventId;

            assert sessionId != null;
            this.sessionId = sessionId;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Delete setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getSessionId()
        {
            return this.sessionId;
        }

        public Delete setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
    }
}

