package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * TracksResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class TracksResponse extends ServerResponse {

    private List<TrackResponse> tracks;

    public TracksResponse()
    {
    }

    public List<TrackResponse> getTracks()
    {
        return this.tracks;
    }

    public TracksResponse setTracks(List<TrackResponse> tracks)
    {
        this.tracks = tracks;
        return this;
    }
}