package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequestData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ModifyFeedbackRequest replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class ModifyFeedbackRequest extends ServerRequestData {

    private String additionalFeedback;

    private Integer contentScore;

    private String eventId;

    private Integer overallScore;

    private Integer relevancyScore;

    private String salt;

    private String sessionId;

    private Integer speakerScore;

    private String userId;

    private Boolean willUse;

    public String getAdditionalFeedback()
    {
        return this.additionalFeedback;
    }

    public ModifyFeedbackRequest setAdditionalFeedback(String additionalFeedback)
    {
        this.additionalFeedback = additionalFeedback;
        return this;
    }

    public Integer getContentScore()
    {
        return this.contentScore;
    }

    public ModifyFeedbackRequest setContentScore(Integer contentScore)
    {
        this.contentScore = contentScore;
        return this;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public ModifyFeedbackRequest setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public Integer getOverallScore()
    {
        return this.overallScore;
    }

    public ModifyFeedbackRequest setOverallScore(Integer overallScore)
    {
        this.overallScore = overallScore;
        return this;
    }

    public Integer getRelevancyScore()
    {
        return this.relevancyScore;
    }

    public ModifyFeedbackRequest setRelevancyScore(Integer relevancyScore)
    {
        this.relevancyScore = relevancyScore;
        return this;
    }

    public String getSalt()
    {
        return this.salt;
    }

    public ModifyFeedbackRequest setSalt(String salt)
    {
        this.salt = salt;
        return this;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public ModifyFeedbackRequest setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
        return this;
    }

    public Integer getSpeakerScore()
    {
        return this.speakerScore;
    }

    public ModifyFeedbackRequest setSpeakerScore(Integer speakerScore)
    {
        this.speakerScore = speakerScore;
        return this;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public ModifyFeedbackRequest setUserId(String userId)
    {
        this.userId = userId;
        return this;
    }

    public Boolean getWillUse()
    {
        return this.willUse;
    }

    public ModifyFeedbackRequest setWillUse(Boolean willUse)
    {
        this.willUse = willUse;
        return this;
    }

    @Override
    public String toJSON()
        throws JSONException {
        return new JSONObject()
                .put("additionalFeedback", additionalFeedback)
                .put("contentScore", contentScore)
                .put("eventId", eventId)
                .put("overallScore", overallScore)
                .put("relevancyScore", relevancyScore)
                .put("salt", salt)
                .put("sessionId", sessionId)
                .put("speakerScore", speakerScore)
                .put("userId", userId)
                .put("willUser", willUse)
                .toString();
    }
}