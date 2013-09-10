package com.funkyandroid.droidcon.uk.droidconsched.io;

import com.funkyandroid.droidcon.uk.droidconsched.io.model.CheckIns;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.Events;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.Users;

/**
 * API for interacting with the server stored data.
 *
 * @author Al Sutton, Funky Android Ltd. (http://funkyandroid.com/)
 */
public class ConferenceAPI {
    public static final String DEFAULT_ROOT_URL = "https://www.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "googledevelopers/v1/";
    public static final String DEFAULT_BASE_URL = "https://www.googleapis.com/googledevelopers/v1/";

    public ConferenceAPI()
    {
    }

    public CheckIns checkIns()
    {
        return new CheckIns();
    }

    public Events events()
    {
        return new Events();
    }

    public Users users()
    {
        return new Users();
    }
}