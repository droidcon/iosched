package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequestData;
import com.google.api.client.util.Key;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 10/09/2013
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class ModifyTodoRequest extends ServerRequestData {

    private Double completionTimestamp;

    private String eventId;

    private String nfcId;

    private String todoId;

    private String userId;

    public ModifyTodoRequest()
    {
    }

    public Double getCompletionTimestamp()
    {
        return this.completionTimestamp;
    }

    public ModifyTodoRequest setCompletionTimestamp(Double completionTimestamp)
    {
        this.completionTimestamp = completionTimestamp;
        return this;
    }

    public String getEventId()
    {
        return this.eventId;
    }

    public ModifyTodoRequest setEventId(String eventId)
    {
        this.eventId = eventId;
        return this;
    }

    public String getNfcId()
    {
        return this.nfcId;
    }

    public ModifyTodoRequest setNfcId(String nfcId)
    {
        this.nfcId = nfcId;
        return this;
    }

    public String getTodoId()
    {
        return this.todoId;
    }

    public ModifyTodoRequest setTodoId(String todoId)
    {
        this.todoId = todoId;
        return this;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public ModifyTodoRequest setUserId(String userId)
    {
        this.userId = userId;
        return this;
    }

    @Override
    public String toJSON() throws JSONException {
        return new JSONObject()
                .put("CompletionTimestamp", completionTimestamp)
                .put("EventId", eventId)
                .put("NfcId", nfcId)
                .put("TodoId", todoId)
                .put("UserId", userId)
                .toString();
    }
}
