package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 17/09/2013
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class Tweets {

    public List list(String eventId)
            throws IOException
    {
        return new List(eventId);
    }


    public class List extends ServerRequest<TweetsResponse>
    {
        private String eventId;

        protected List(String eventId)
        {
            super("GET", "events/{eventId}/tweets/current", null, TweetsResponse.class);
            this.eventId = eventId;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }
    }
}
