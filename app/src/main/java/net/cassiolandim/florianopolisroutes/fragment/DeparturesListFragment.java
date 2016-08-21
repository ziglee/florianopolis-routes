package net.cassiolandim.florianopolisroutes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.activity.DetailsActivity;
import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.model.RestApiFindDeparturesResponse;

import retrofit2.Call;

public class DeparturesListFragment extends ParentFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        long routeId = getArguments().getLong(DetailsActivity.EXTRAS_ID);

        View rootView = inflater.inflate(R.layout.fragment_departures_list, container, false);

        Call<RestApiFindDeparturesResponse> call = buildFindDeparturesByRouteIdCall(routeId);

        return rootView;
    }

    private Call<RestApiFindDeparturesResponse> buildFindDeparturesByRouteIdCall(long routeId) {
        BackendApiClient apiClient = buildBackendApiClient();
        return apiClient.findDeparturesByRouteId(buildAuthorizationString(),
                "staging",
                buildRequestBody("routeId", String.valueOf(routeId)));
    }
}
