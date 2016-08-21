package net.cassiolandim.florianopolisroutes.backend;

import net.cassiolandim.florianopolisroutes.model.RestApiFindDeparturesResponse;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRoutesResponse;
import net.cassiolandim.florianopolisroutes.model.RestApiFindStopsResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BackendApiClient {

    @POST("/v1/queries/findRoutesByStopName/run")
    Call<RestApiFindRoutesResponse> findRoutesByStopName(@Header("Authorization") String authorization,
                                                         @Header("X-AppGlu-Environment") String environment,
                                                         @Body RequestBody body);

    @POST("/v1/queries/findStopsByRouteId/run")
    Call<RestApiFindStopsResponse> findStopsByRouteId(@Header("Authorization") String authorization,
                                                      @Header("X-AppGlu-Environment") String environment,
                                                      @Body RequestBody body);

    @POST("/v1/queries/findDeparturesByRouteId/run")
    Call<RestApiFindDeparturesResponse> findDeparturesByRouteId(@Header("Authorization") String authorization,
                                                                @Header("X-AppGlu-Environment") String environment,
                                                                @Body RequestBody body);
}
