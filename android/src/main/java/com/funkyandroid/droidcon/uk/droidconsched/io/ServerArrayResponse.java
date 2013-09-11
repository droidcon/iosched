package com.funkyandroid.droidcon.uk.droidconsched.io;

import com.funkyandroid.droidcon.uk.droidconsched.io.model.PresenterResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Response which contains an array of other objects.
 */
public abstract class ServerArrayResponse<T extends ServerResponse> extends ServerResponse {

    /**
     * The elements held in the array.
     */
    private List<T> arrayContents;

    /**
     * Get the name of the attribute which holds the array of objects
     *
     * @return the name of the JSON attribute which holds the array.
     */

    protected abstract String getArrayAttributeName();

    /**
     * Create a new instance of the class this array holds
     */

    protected abstract T getNewObject();

    /**
     * Get the array of objects
     */

    protected List<T> getArrayContents() {
        return arrayContents;
    }

    /**
     * Read the name of the
     * @param jsonObject The object to get the property data from.
     * @throws JSONException
     */
    @Override
    public void fromJSON(final JSONObject jsonObject)
            throws JSONException {
        arrayContents = new ArrayList<T>();
        if(!jsonObject.has(getArrayAttributeName())) {
            return;
        }

        JSONArray jsonArray = jsonObject.getJSONArray(getArrayAttributeName());
        for(int i = 0 ; i < jsonArray.length() ; i++) {
            T object = getNewObject();
            object.fromJSON(jsonArray.getJSONObject(i));
            arrayContents.add(object);
        }
    }
}
