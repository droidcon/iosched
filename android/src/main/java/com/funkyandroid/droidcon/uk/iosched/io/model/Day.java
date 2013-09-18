/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.funkyandroid.droidcon.uk.iosched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerArrayResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Day extends ServerArrayResponse<TimeSlot> {
    public String date;

    public List<TimeSlot> getSlots()
    {
        return getArrayContents();
    }

    @Override
    protected String getArrayAttributeName() {
        return "slot";
    }

    @Override
    protected TimeSlot getNewObject() {
        return new TimeSlot();
    }

    /**
     * Read the name of the
     * @param jsonObject The object to get the property data from.
     * @throws JSONException
     */
    @Override
    public void fromJSON(final JSONObject jsonObject)
            throws JSONException {
        date = jsonObject.getString("date");
        super.fromJSON(jsonObject);
    }

}
