package com.funkyandroid.droidcon.uk.droidconsched.io.model.events.sessions;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.FeedbackResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.ModifyFeedbackRequest;

/**
 * Events.Sessions.Feedback as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Feedback extends ServerRequest<FeedbackResponse>
{
    private static final String REST_PATH = "events/{eventId}/sessions/{sessionId}/feedback";

    private String eventId;

    private String sessionId;

    public Feedback(String eventId, String sessionId, ModifyFeedbackRequest content)
    {
        super("PUT", "events/{eventId}/sessions/{sessionId}/feedback", content, FeedbackResponse.class);
        assert eventId != null;
        this.eventId = eventId;

        assert sessionId != null;
        this.sessionId = sessionId;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public Feedback setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public Feedback setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
}
