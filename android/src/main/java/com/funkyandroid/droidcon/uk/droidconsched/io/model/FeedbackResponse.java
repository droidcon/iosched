package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

/**
 * FeedbackResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class FeedbackResponse extends ServerResponse {

    private String additionalFeedback;

    private Integer contentScore;

    private String eventId;

    private Integer overallScore;

    private Integer relevancyScore;

    private String sessionId;

    private Integer speakerScore;

    private Boolean willUse;

    public FeedbackResponse()
    {
    }

    public String getAdditionalFeedback()
    {
        return this.additionalFeedback;
    }

    public FeedbackResponse setAdditionalFeedback(String additionalFeedback)
    {
        this.additionalFeedback = additionalFeedback;
        return this;
    }

    public Integer getContentScore()
    {
        return this.contentScore;
    }

    public FeedbackResponse setContentScore(Integer contentScore)
    {
        this.contentScore = contentScore;
        return this;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public FeedbackResponse setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public Integer getOverallScore()
    {
        return this.overallScore;
    }

    public FeedbackResponse setOverallScore(Integer overallScore)
    {
        this.overallScore = overallScore;
        return this;
    }

    public Integer getRelevancyScore()
    {
        return this.relevancyScore;
    }

    public FeedbackResponse setRelevancyScore(Integer relevancyScore)
    {
        this.relevancyScore = relevancyScore;
        return this;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public FeedbackResponse setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
        return this;
    }

    public Integer getSpeakerScore()
    {
        return this.speakerScore;
    }

    public FeedbackResponse setSpeakerScore(Integer speakerScore)
    {
        this.speakerScore = speakerScore;
        return this;
    }

    public Boolean getWillUse()
    {
        return this.willUse;
    }

    public FeedbackResponse setWillUse(Boolean willUse)
    {
        this.willUse = willUse;
        return this;
    }
}
