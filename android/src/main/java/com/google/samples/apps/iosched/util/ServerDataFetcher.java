package com.google.samples.apps.iosched.util;

import android.text.TextUtils;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.ConsoleRequestLogger;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.RequestLogger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import static com.google.samples.apps.iosched.util.LogUtils.LOGD;
import static com.google.samples.apps.iosched.util.LogUtils.LOGE;
import static com.google.samples.apps.iosched.util.LogUtils.LOGW;

/**
 * Common methods for classes which want to implement a "Fetch on change" policy
 * for server data.
 */

public class ServerDataFetcher {
    // timestamp of the file on the server
    private String mServerTimestamp = null;

    /**
     * Fetches data from the remote server.
     *
     * @param url The URL to fetch data from
     * @param refTimestamp The timestamp of the data to use as a reference; if the remote data
     *                     is not newer than this timestamp, no data will be downloaded and
     *                     this method will return null.
     *
     * @return The data downloaded, or null if there is no data to download
     * @throws java.io.IOException if an error occurred during download.
     */
    public String fetchDataIfNewer(String logTag, String url, String refTimestamp)
            throws IOException {
        if (TextUtils.isEmpty(url)) {
            LOGW(logTag, "Manifest URL is empty (remote sync disabled!).");
            return null;
        }

        BasicHttpClient httpClient = getQuietBasicHttpClient();

        // Only download if data is newer than refTimestamp
        // Cloud Storage is very picky with the If-Modified-Since format. If it's in a wrong
        // format, it refuses to serve the file, returning 400 HTTP error. So, if the
        // refTimestamp is in a wrong format, we simply ignore it. But pay attention to this
        // warning in the log, because it might mean unnecessary data is being downloaded.
        if (!TextUtils.isEmpty(refTimestamp)) {
            if (TimeUtils.isValidFormatForIfModifiedSinceHeader(refTimestamp)) {
                httpClient.addHeader("If-Modified-Since", refTimestamp);
            } else {
                LOGW(logTag, "Could not set If-Modified-Since HTTP header. Potentially downloading " +
                        "unnecessary data. Invalid format of refTimestamp argument: "+refTimestamp);
            }
        }

        HttpResponse response = httpClient.get(url, null);
        if (response == null) {
            LOGE(logTag, "Request for manifest returned null response.");
            throw new IOException("Request for data manifest returned null response.");
        }

        int status = response.getStatus();
        if (status == HttpURLConnection.HTTP_OK) {
            LOGD(logTag, "Server returned HTTP_OK, so new data is available.");
            mServerTimestamp = getLastModified(response);
            LOGD(logTag, "Server timestamp for new data is: " + mServerTimestamp);
            return response.getBodyAsString();
        } else if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
            // data on the server is not newer than our data
            LOGD(logTag, "HTTP_NOT_MODIFIED: data has not changed since " + refTimestamp);
            return null;
        } else {
            LOGE(logTag, "Error fetching conference data: HTTP status " + status);
            throw new IOException("Error fetching conference data: HTTP status " + status);
        }
    }

    protected BasicHttpClient getQuietBasicHttpClient() {
        BasicHttpClient httpClient = new BasicHttpClient();
        httpClient.setRequestLogger(mQuietLogger);
        return httpClient;
    }

    // Returns the timestamp of the data downloaded from the server
    public String getServerDataTimestamp() {
        return mServerTimestamp;
    }

    private String getLastModified(HttpResponse resp) {
        if (!resp.getHeaders().containsKey("Last-Modified")) {
            return "";
        }

        List<String> s = resp.getHeaders().get("Last-Modified");
        return s.isEmpty() ? "" : s.get(0);
    }
    /**
     * A type of ConsoleRequestLogger that does not log requests and responses.
     */
    private RequestLogger mQuietLogger = new ConsoleRequestLogger(){
        @Override
        public void logRequest(HttpURLConnection uc, Object content) throws IOException { }

        @Override
        public void logResponse(HttpResponse res) { }
    };
}
