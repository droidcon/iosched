package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * PresentersResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class PresentersResponse extends ServerResponse {

    private List<PresenterResponse> presenters;

    public PresentersResponse()
    {
    }

    public List<PresenterResponse> getPresenters()
    {
        return this.presenters;
    }

    public PresentersResponse setPresenters(List<PresenterResponse> presenters)
    {
        this.presenters = presenters;
        return this;
    }
}