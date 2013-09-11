package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerArrayResponse;

import java.util.List;

/**
 * PresentersResponse replacement for the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class PresentersResponse extends ServerArrayResponse<PresenterResponse> {

    public List<PresenterResponse> getPresenters()
    {
        return getArrayContents();
    }

    @Override
    protected String getArrayAttributeName() {
        return "presenters";
    }

    @Override
    protected PresenterResponse getNewObject() {
        return new PresenterResponse();
    }
}