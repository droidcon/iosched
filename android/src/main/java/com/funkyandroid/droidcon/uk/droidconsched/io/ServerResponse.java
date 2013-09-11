package com.funkyandroid.droidcon.uk.droidconsched.io;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Base Class for classes which provide data returned from the server.
 */
public class ServerResponse {
    /**
     * The parameter for a null setter
     */

    private static final Object[] NULL_SETTER_PARAMETERS = { null };

    /**
     * The setter map for this class.
     */

    private Map<String, Method> mSetterMap = null;

    /**
     * Set the properties in this object using the data in the JSON representation.
     *
     * @param jsonObject The object to get the property data from.
     */

    public void fromJSON(final JSONObject jsonObject)
        throws IllegalAccessException, InvocationTargetException, JSONException {
        setPropertiesByReflection(jsonObject);
    }

    /**
     * Set all of the properties in this object using reflection. If a property in
     * the JSON object isn't found in the object it's ignored, this allows older
     * client to remain compatible with newer server responses.
     *
     * @param jsonObject The JSON object to extract the data from.
     */

    void setPropertiesByReflection(final JSONObject jsonObject)
            throws IllegalAccessException, InvocationTargetException, JSONException {

        // Build a map of the setters in this object if it doesn't exist.
        synchronized (this) {
            if(mSetterMap == null) {
                mSetterMap = new HashMap<String, Method>();
                Method[] methods = getClass().getMethods();
                for(Method method : methods) {
                    final String methodName = method.getName();
                    // Skip methods with more than one parameter
                    if(method.getParameterTypes().length != 1) {
                        continue;
                    }
                    // Skip non-setters
                    if(!methodName.startsWith("set")) {
                        continue;
                    }

                    // Change setBlah to be blah to reflect the attribute name in the JSON representation
                    mSetterMap.put(Character.toLowerCase(methodName.charAt(3))+methodName.substring(4), method);
                }
            }
        }


        // JSONObject is backed
        Iterator keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.toString();
            if(!mSetterMap.containsKey(key)) {
                continue;
            }

            Method method = mSetterMap.get(key);

            if      (jsonObject.isNull(key)) {
                method.invoke(this, NULL_SETTER_PARAMETERS);
                continue;
            }

            Class setterType = method.getParameterTypes()[0];

            if          (setterType.equals(String.class)) {
                method.invoke(this, jsonObject.getString(key));
            } else if   (setterType.equals(Double.class)) {
                method.invoke(this, jsonObject.getDouble(key));
            } else if   (setterType.equals(Long.class)) {
                method.invoke(this, jsonObject.getLong(key));
            } else if   (setterType.equals(Boolean.class)) {
                method.invoke(this, jsonObject.getBoolean(key));
            } else if   (setterType.equals(Integer.class)) {
                method.invoke(this, jsonObject.getInt(key));
            } else if   (setterType.equals(JSONArray.class)) {
                method.invoke(this, jsonObject.getJSONArray(key));
            } else if   (setterType.equals(JSONObject.class)) {
                method.invoke(this, jsonObject.getJSONObject(key));
            } else {
                Log.w("Server Response", "Unable to deal with setter for "+key+" in "+getClass().getCanonicalName());
            }
        }
    }
}
