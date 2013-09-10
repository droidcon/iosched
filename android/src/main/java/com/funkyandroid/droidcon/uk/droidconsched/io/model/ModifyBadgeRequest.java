package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequestData;
import com.google.api.client.util.Key;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 10/09/2013
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class ModifyBadgeRequest extends ServerRequestData {

    private String eventId;

    private Boolean forceNfcId;

    private String nfcId;

    private String userId;

    public ModifyBadgeRequest()
    {
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public ModifyBadgeRequest setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public Boolean getForceNfcId()
    {
        return this.forceNfcId;
    }

    public ModifyBadgeRequest setForceNfcId(Boolean forceNfcId)
    {
        this.forceNfcId = forceNfcId;
        return this;
    }

    public String getNfcId()
    {
        return this.nfcId;
    }

    public ModifyBadgeRequest setNfcId(String nfcId)
    {
        this.nfcId = nfcId;
        return this;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public ModifyBadgeRequest setUserId(String userId)
    {
        this.userId = userId;
        return this;
    }

    @Override
    public String toJSON()
        throws JSONException {
        return new JSONObject()
                .put("EventId", eventId)
                .put("ForceNfcId", forceNfcId)
                .put("NfcId", nfcId)
                .put("UserId", userId)
                .toString();
    }
}
