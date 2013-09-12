package com.funkyandroid.droidcon.uk.droidconsched.io;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Base Class for classes which request data from the server
 */
public class ServerRequest<T extends ServerResponse> {

    /**
     * The URL root for Droidcon data
     */

    private static final String DATA_ROOT = "http://dcuk2013.funkyandroid.net/";

    /**
     * The parameter types for the getters to use to fill URL parameters
     */

    private static final Class[] GETTER_PARAMETERS = {};

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

    /**
     * Perform the request
     *
     * @return The appropriate ServerResponse object
     * @throws IOException Thrown if there is a problem making the request.
     */
    public T execute()
        throws IOException {
        HttpURLConnection connection;
        try {
            URL requestURL = new URL(DATA_ROOT+expandURLParameters());
            connection = (HttpURLConnection)requestURL.openConnection();
        } catch (NoSuchMethodException e) {
            throw new IOException("Error constructing request URL", e);
        } catch (IllegalAccessException e) {
            throw new IOException("Error constructing request URL", e);
        } catch (InvocationTargetException e) {
            throw new IOException("Error constructing request URL", e);
        }

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

    /**
     * Replace the parameters in a URL with the values from this object.
     *
     * @return the RequestURL with the parameters expanded.
     */

    private String expandURLParameters()
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder result = new StringBuilder(mRequestURL.length()+16);

        StringTokenizer stringTokenizer = new StringTokenizer(mRequestURL, "/");
        StringBuilder getterName = new StringBuilder(128);
        while(stringTokenizer.hasMoreTokens()) {
            String urlSection = stringTokenizer.nextToken();
            if(!urlSection.startsWith("{") || !urlSection.endsWith("}")) {
                result.append(urlSection);
                result.append('/');
                continue;
            }

            if(getterName.length() != 0) {
                getterName.delete(0, getterName.length());
            }

            getterName.append("get");
            getterName.append(Character.toUpperCase(urlSection.charAt(1)));
            getterName.append(urlSection.substring(2, urlSection.length()-1));
            Method getter = getClass().getMethod(getterName.toString(), GETTER_PARAMETERS);
            Object value = getter.invoke(this, null);
            result.append(value.toString());
            result.append('/');
        }

        if(!mRequestURL.endsWith("/")) {
            result.deleteCharAt(result.length()-1);
        }

        return result.toString();
    }
}
