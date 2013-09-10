package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerResponse;

import java.util.List;

/**
 * TodosResponse as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class TodosResponse extends ServerResponse {

    private String nfcId;

    private String registrationStatus;

    private List<TodoResponse> todos;

    public TodosResponse()
    {
    }

    public String getNfcId()
    {
        return this.nfcId;
    }

    public TodosResponse setNfcId(String nfcId)
    {
        this.nfcId = nfcId;
        return this;
    }

    public String getRegistrationStatus()
    {
        return this.registrationStatus;
    }

    public TodosResponse setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
        return this;
    }

    public List<TodoResponse> getTodos()
    {
        return this.todos;
    }

    public TodosResponse setTodos(List<TodoResponse> todos)
    {
        this.todos = todos;
        return this;
    }
}
