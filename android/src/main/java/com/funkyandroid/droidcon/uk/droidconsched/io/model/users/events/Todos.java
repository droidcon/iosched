package com.funkyandroid.droidcon.uk.droidconsched.io.model.users.events;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.ModifyTodoRequest;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TodoResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TodosResponse;

import java.io.IOException;

/**
 * Users.Events.Todos as modelled in the Google Developers API.
 *
 * This is an implementation which replaces the generated version which shipped with IOsched and
 * makes the code human more human readable and removed the dependence on additional Google libraries.
 */
public class Todos
{
    public Todos()
    {
    }

    public Get get(String userId, String eventId, String todoId)
            throws IOException
    {
        Get result = new Get(userId, eventId, todoId);
        return result;
    }

    public List list(String userId, String eventId)
            throws IOException
    {
        List result = new List(userId, eventId);
        return result;
    }

    public Patch patch(String userId, String eventId, String todoId, ModifyTodoRequest content)
            throws IOException
    {
        Patch result = new Patch(userId, eventId, todoId, content);
        return result;
    }

    public Update update(String userId, String eventId, String todoId, ModifyTodoRequest content)
            throws IOException
    {
        Update result = new Update(userId, eventId, todoId, content);
        return result;
    }

    public class Update extends ServerRequest<TodosResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/todos/{todoId}";

        private String userId;

        private String eventId;

        private String todoId;

        protected Update(String userId, String eventId, String todoId, ModifyTodoRequest content)
        {
            super("PUT", "users/{userId}/events/{eventId}/todos/{todoId}", content, TodosResponse.class);

            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;

            assert todoId != null;
            this.todoId = todoId;
        }

        public String getUserId()
        {
            return this.userId;
        }

        public Update setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Update setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getTodoId()
        {
            return this.todoId;
        }

        public Update setTodoId(String todoId) {
            this.todoId = todoId;
            return this;
        }
    }

    public class Patch extends ServerRequest<TodosResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/todos/{todoId}";

        private String userId;

        private String eventId;

        private String todoId;

        protected Patch(String userId, String eventId, String todoId, ModifyTodoRequest content)
        {
            super("PATCH", "users/{userId}/events/{eventId}/todos/{todoId}", content, TodosResponse.class);
            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;

            assert todoId != null;
            this.todoId = todoId;
        }

        public String getUserId()
        {
            return this.userId;
        }

        public Patch setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Patch setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getTodoId()
        {
            return this.todoId;
        }

        public Patch setTodoId(String todoId) {
            this.todoId = todoId;
            return this;
        }
    }

    public class List extends ServerRequest<TodosResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/todos";

        private String userId;

        private String eventId;

        protected List(String userId, String eventId)
        {
            super("GET", "users/{userId}/events/{eventId}/todos", null, TodosResponse.class);

            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;
        }

        public String getUserId()
        {
            return this.userId;
        }

        public List setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public List setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
    }

    public class Get extends ServerRequest<TodoResponse>
    {
        private static final String REST_PATH = "users/{userId}/events/{eventId}/todos/{todoId}";

        private String userId;

        private String eventId;

        private String todoId;

        protected Get(String userId, String eventId, String todoId)
        {
            super("GET", "users/{userId}/events/{eventId}/todos/{todoId}", null, TodoResponse.class);

            assert userId != null;
            this.userId = userId;

            assert eventId != null;
            this.eventId = eventId;

            assert todoId != null;
            this.todoId = todoId;
        }

        public String getUserId()
        {
            return this.userId;
        }

        public Get setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getEventId()
        {
            return this.eventId;
        }

        public Get setEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public String getTodoId()
        {
            return this.todoId;
        }

        public Get setTodoId(String todoId) {
            this.todoId = todoId;
            return this;
        }
    }
}
