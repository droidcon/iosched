package com.funkyandroid.droidcon.uk.droidconsched.io.model.users.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.ModifyBadgeRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.RegistrationResponse;

import java.io.IOException;

/**
 * Users.Events.Badge as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Badge
{
    public Badge()
    {
    }

    public Delete delete(String userId, String eventId)
            throws IOException
    {
        return new Delete(userId, eventId);
    }

    public Update update(String userId, String eventId, ModifyBadgeRequest content)
            throws IOException
    {
        return new Update(userId, eventId, content);
    }

    public class Update extends ServerRequest<RegistrationResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/badge";

        private String userId;

        private String eventId;

        protected Update(String userId, String eventId, ModifyBadgeRequest content)
        {
            super("PUT", "users/{userId}/events/{eventId}/badge", content, RegistrationResponse.class);
            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;
        }

        public String getUserId()
        {
            return this.userId;
        }

        public Update setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Update setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
    }

    public class Delete extends ServerRequest<RegistrationResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/badge";

        private String userId;

        private String eventId;

        protected Delete(String userId, String eventId)
        {
            super("DELETE", "users/{userId}/events/{eventId}/badge", null, RegistrationResponse.class);
            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;
        }
        public String getUserId()
        {
            return this.userId;
        }

        public Delete setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Delete setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
    }
}
