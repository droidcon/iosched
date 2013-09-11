package com.funkyandroid.droidcon.uk.droidconsched.io;

import android.util.Log;
import com.squareup.okhttp.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base Class for classes which request data from the server
 */
public class ServerRequest<T extends ServerResponse> {

    /**
     * The URL root for Droidcon data
     */

    private static final String DATA_ROOT = "http://dcuk2013.funkyandroid.net/";

    /**
     * The client to use to make the request
     */

    private OkHttpClient client = new OkHttpClient();

    /**
     * The request type to make
     */

    private String mRequestType;

    /**
     * The relative URL to connect to. This is relative to the URL for the base of the
     * server application
     */

    private String mRequestURL;

    /**
     * The data to be sent to the server during the request. null means no data.
     */

    private ServerRequestData mRequestContent;

    /**
     * The response class to populate.
     */

    private Class<T> mResponseClass;

    /**
     * Construct a server request.
     *
     * @param requestType The type of request.
     * @param requestURL The relative URL for the request endpoint.
     * @param requestContent The content of the request.
     * @param responseClass The class to be used for the response.
     */

    public ServerRequest(final String requestType, final String requestURL,
                         final ServerRequestData requestContent,
                         final Class<T> responseClass) {
        mRequestType = requestType;
        mRequestURL = requestURL;
        mRequestContent = requestContent;
        mResponseClass = responseClass;
    }

    public T execute()
        throws IOException {
        HttpURLConnection connection = client.open(new URL(DATA_ROOT+mRequestURL));
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            connection.setRequestMethod(mRequestType);
            if(mRequestContent != null) {
                outputStream = connection.getOutputStream();
                outputStream.write(mRequestContent.toJSON().getBytes("UTD-8"));
                outputStream.close();
                outputStream = null;
            }

            T result = mResponseClass.newInstance();
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK ) {
                Log.w("DCsched", "Response error from server :" + responseCode);
                return result;
            }

            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();
            inputStream = null;

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            result.fromJSON(jsonObject);
        } catch (JSONException e) {
            throw new IOException("Formatting error", e);
        } catch (InstantiationException e) {
            throw new IOException("Problem creating response object", e);
        } catch (IllegalAccessException e) {
            throw new IOException("Problem creating response object", e);
        } catch (InvocationTargetException e) {
            throw new IOException("Problem parsing response", e);
        } finally {
            if(outputStream != null) {
                outputStream.close();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }
}
