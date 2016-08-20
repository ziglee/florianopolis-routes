package net.cassiolandim.florianopolisroutes.model;

import java.util.HashMap;
import java.util.Map;

public class RestApiFindRouteRequest {

    public Map<String, String> params = new HashMap<>();

    public RestApiFindRouteRequest() {
    }

    public RestApiFindRouteRequest(String key, String value) {
        params.put(key, value);
    }

}
