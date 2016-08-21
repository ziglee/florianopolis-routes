package net.cassiolandim.florianopolisroutes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.activity.DetailsActivity;
import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.model.RestApiFindStopsResponse;
import net.cassiolandim.florianopolisroutes.model.StopListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopsListFragment extends ParentFragment {

    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final long routeId = getArguments().getLong(DetailsActivity.EXTRAS_ID);

        View rootView = inflater.inflate(R.layout.fragment_stops_list, container, false);
        this.mListView = (ListView) rootView.findViewById(android.R.id.list);

        Call<RestApiFindStopsResponse> call = buildFindStopsByRouteIdCall(routeId);

        call.enqueue(new Callback<RestApiFindStopsResponse>() {
            @Override
            public void onResponse(Call<RestApiFindStopsResponse> call, Response<RestApiFindStopsResponse> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindStopsResponse decodedResponse = response.body();
                if (decodedResponse == null) {
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                StopListAdapter adapter = new StopListAdapter(StopsListFragment.this.getContext(), decodedResponse.rows);
                mListView.setAdapter(adapter);
            }

            public void onFailure(Call<RestApiFindStopsResponse> call, Throwable t) {
                Log.w("findRoutesByStopName", String.valueOf(routeId), t);
                Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private Call<RestApiFindStopsResponse> buildFindStopsByRouteIdCall(long routeId) {
        BackendApiClient apiClient = buildBackendApiClient();
        return apiClient.findStopsByRouteId(buildAuthorizationString(),
                "staging",
                buildRequestBody("routeId", String.valueOf(routeId)));
    }

    private static class StopListAdapter extends BaseAdapter {

        Context context;
        List<StopListItem> items;

        public StopListAdapter(Context context, List<StopListItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            StopListItem item = (StopListItem) getItem(position);
            return item.id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.stop_list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.name);

            StopListItem item = (StopListItem) getItem(position);
            name.setText(item.name);

            return convertView;
        }
    }
}
