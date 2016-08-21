package net.cassiolandim.florianopolisroutes.model;

import java.util.HashMap;
import java.util.Map;

public class RestApiRequest {

    public Map<String, String> params = new HashMap<>();

    public RestApiRequest() {
    }

    public RestApiRequest(String key, String value) {
        params.put(key, value);
    }

}
