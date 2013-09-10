package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * SessionsResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class SessionsResponse extends ServerResponse {

    private List<SessionResponse> sessions;

    public SessionsResponse()
    {
    }

    public List<SessionResponse> getSessions()
    {
        return this.sessions;
    }

    public SessionsResponse setSessions(List<SessionResponse> sessions)
    {
        this.sessions = sessions;
        return this;
    }
}