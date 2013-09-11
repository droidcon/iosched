package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerArrayResponse;

import java.util.List;

/**
 * SessionsResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class SessionsResponse extends ServerArrayResponse<SessionResponse> {

    public List<SessionResponse> getSessions()
    {
        return getArrayContents();
    }

    @Override
    protected String getArrayAttributeName() {
        return "sessions";
    }

    @Override
    protected SessionResponse getNewObject() {
        return new SessionResponse();
    }
}