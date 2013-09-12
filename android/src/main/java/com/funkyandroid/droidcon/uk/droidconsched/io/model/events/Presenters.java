package com.funkyandroid.droidcon.uk.droidconsched.io.model.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.PresenterResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.PresentersResponse;

import java.io.IOException;

/**
 * Events.Presenters as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Presenters
{
    public Presenters()
    {
    }

    public Get get(String eventId, String presenterId)
            throws IOException
    {
        Get result = new Get(eventId, presenterId);
        return result;
    }

    public List list(String eventId)
            throws IOException
    {
        List result = new List(eventId);
        return result;
    }

    public class List extends ServerRequest<PresentersResponse>
    {
        private static final String REST_PATH = "events/{eventId}/presenters";

        private String eventId;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/presenters", null, PresentersResponse.class);
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

    public class Get extends ServerRequest<PresenterResponse>
    {
        private static final String REST_PATH = "events/{eventId}/presenters/{presenterId}";

        private String eventId;

        private String presenterId;

        protected Get(String eventId, String presenterId)
        {
            super("GET", "events/{eventId}/presenters/{presenterId}", null, PresenterResponse.class);
            assert presenterId != null;
            this.presenterId = presenterId;

            assert eventId != null;
            this.eventId = eventId;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Get setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getPresenterId()
        {
            return this.presenterId;
        }

        public Get setPresenterId(String presenterId) {
            this.presenterId = presenterId;
            return this;
        }
    }
}
