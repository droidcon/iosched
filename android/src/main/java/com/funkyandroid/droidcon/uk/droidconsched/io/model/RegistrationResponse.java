package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 10/09/2013
 * Time: 14:09
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationResponse extends ServerResponse {

    private Double checkinTimestamp;

    private String confirmationCode;

    private String counterName;

    private String eventId;

    private String nfcId;

    private String userId;

    public RegistrationResponse()
    {
    }

    public Double getCheckinTimestamp()
    {
        return this.checkinTimestamp;
    }

    public RegistrationResponse setCheckinTimestamp(Double checkinTimestamp)
    {
        this.checkinTimestamp = checkinTimestamp;
        return this;
    }

    public String getConfirmationCode()
    {
        return this.confirmationCode;
    }

    public RegistrationResponse setConfirmationCode(String confirmationCode)
    {
        this.confirmationCode = confirmationCode;
        return this;
    }

    public String getCounterName()
    {
        return this.counterName;
    }

    public RegistrationResponse setCounterName(String counterName)
    {
        this.counterName = counterName;
        return this;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public RegistrationResponse setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public String getNfcId()
    {
        return this.nfcId;
    }

    public RegistrationResponse setNfcId(String nfcId)
    {
        this.nfcId = nfcId;
        return this;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public RegistrationResponse setUserId(String userId)
    {
        this.userId = userId;
        return this;
    }
}