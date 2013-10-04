package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * SessionResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class SessionResponse extends ServerResponse {

    private String description;

    private Double endTimestamp;

    private String iconUrl;

    private String id;

    private String location;

    private List<String> presenterIds;

    private Double startTimestamp;

    private String subtype;

    private String title;

    private String webLink;

    private int flags;

    public SessionResponse()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public SessionResponse setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public Double getEndTimestamp()
    {
        return this.endTimestamp;
    }

    public SessionResponse setEndTimestamp(Double endTimestamp)
    {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    public SessionResponse setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
        return this;
    }

    public String getId()
    {
        return this.id;
    }

    public SessionResponse setId(String id)
    {
        this.id = id;
        return this;
    }

    public String getLocation()
    {
        return this.location;
    }

    public SessionResponse setLocation(String location)
    {
        this.location = location;
        return this;
    }

    public List<String> getPresenterIds()
    {
        return this.presenterIds;
    }

    public SessionResponse setPresenterIds(List<String> presenterIds)
    {
        this.presenterIds = presenterIds;
        return this;
    }

    public Double getStartTimestamp()
    {
        return this.startTimestamp;
    }

    public SessionResponse setStartTimestamp(Double startTimestamp)
    {
        this.startTimestamp = startTimestamp;
        return this;
    }

    public String getSubtype()
    {
        return this.subtype;
    }

    public SessionResponse setSubtype(String subtype)
    {
        this.subtype = subtype;
        return this;
    }

    public String getTitle()
    {
        return this.title;
    }

    public SessionResponse setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    @Override
    protected boolean useClassSpecificSetter(JSONObject jsonObject, final String key)
        throws JSONException {
        if(!"presenterIds".equals(key)) {
            return false;
        }

        JSONArray array = jsonObject.getJSONArray("presenterIds");
        List<String> presenterIdList = new ArrayList<String>(array.length());
        for(int i = 0 ; i < array.length() ; i++) {
            presenterIdList.add(array.getString(i));
        }
        setPresenterIds(presenterIdList);
        return true;
    }
}