package com.funkyandroid.droidcon.uk.droidconsched.io;

/**
 * Base Class for classes which request data from the server
 */
public class ServerRequest<T extends ServerResponse> {

    public ServerRequest(final String requestType, final String requestURL,
                         final ServerRequestData requestContent, final Class responseClass) {
    }

    public T execute() {
        return null;
    }
}
