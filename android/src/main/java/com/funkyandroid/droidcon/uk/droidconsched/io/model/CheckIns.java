package com.funkyandroid.droidcon.uk.droidconsched.io.model;

import com.funkyandroid.droidcon.uk.droidconsched.io.ServerRequest;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alsutton
 * Date: 10/09/2013
 * Time: 08:40
 * To change this template use File | Settings | File Templates.
 */
public class CheckIns
{
    public CheckIns()
    {
    }

    public Delete delete(String confirmationCode)
            throws IOException
    {
        return new Delete(confirmationCode);
    }

    public Update update(String confirmationCode, ModifyCheckinRequest content)
            throws IOException
    {
        return new Update(confirmationCode, content);
    }

    public class Update extends ServerRequest<RegistrationResponse>
    {
        private static final String REST_PATH = "checkins/{confirmationCode}";

        private String confirmationCode;

        protected Update(String confirmationCode, ModifyCheckinRequest content)
        {
            super("PUT", "checkins/{confirmationCode}", content, RegistrationResponse.class);
            assert confirmationCode != null;
            this.confirmationCode = confirmationCode;
        }
        public String getConfirmationCode()
        {
            return this.confirmationCode;
        }

        public Update setConfirmationCode(String confirmationCode) {
            this.confirmationCode = confirmationCode;
            return this;
        }
    }

    public class Delete extends ServerRequest<RegistrationResponse>
    {
        private static final String REST_PATH = "checkins/{confirmationCode}";

        private String confirmationCode;

        protected Delete(String confirmationCode)
        {
            super("DELETE", "checkins/{confirmationCode}", null, RegistrationResponse.class);
            assert confirmationCode != null;
            this.confirmationCode = confirmationCode;
        }
        public String getConfirmationCode()
        {
            return this.confirmationCode;
        }

        public Delete setConfirmationCode(String confirmationCode) {
            this.confirmationCode = confirmationCode;
            return this;
        }
    }
}
