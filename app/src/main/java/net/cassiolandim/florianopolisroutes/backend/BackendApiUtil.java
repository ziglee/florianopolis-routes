package net.cassiolandim.florianopolisroutes.backend;

import android.util.Base64;

public class BackendApiUtil {

    public final static String authorizationHeaderValue(String username, String password) {
        return "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
    }
}
