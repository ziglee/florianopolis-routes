package net.cassiolandim.florianopolisroutes.backend;

import net.cassiolandim.florianopolisroutes.model.RestApiFindRouteResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BackendApiClient {

    @POST("/v1/queries/findRoutesByStopName/run")
    Call<RestApiFindRouteResponse> findRoutesByStopName(@Header("Authorization") String authorization,
                                                        @Header("X-AppGlu-Environment") String environment,
                                                        @Body RequestBody body);

}
