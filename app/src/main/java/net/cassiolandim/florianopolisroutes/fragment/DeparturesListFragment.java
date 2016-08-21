package net.cassiolandim.florianopolisroutes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.model.RestApiFindDeparturesResponse;
import net.cassiolandim.florianopolisroutes.model.DepartureListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeparturesListFragment extends ParentFragment {

    private long routeId;
    private ListView mListView;
    private ProgressBar mLoadingSpinner;

    public static DeparturesListFragment newInstance(long routeId) {
        Bundle args = new Bundle();
        args.putLong("routeId", routeId);

        DeparturesListFragment fragment = new DeparturesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routeId = getArguments().getLong("routeId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_departures_list, container, false);
        this.mListView = (ListView) rootView.findViewById(android.R.id.list);
        this.mLoadingSpinner = (ProgressBar) rootView.findViewById(android.R.id.progress);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Call<RestApiFindDeparturesResponse> call = buildFindDeparturesByRouteIdCall(routeId);
        call.enqueue(new Callback<RestApiFindDeparturesResponse>() {
            @Override
            public void onResponse(Call<RestApiFindDeparturesResponse> call, Response<RestApiFindDeparturesResponse> response) {
                if (!response.isSuccessful()) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(DeparturesListFragment.this.getContext(), "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindDeparturesResponse decodedResponse = response.body();
                if (decodedResponse == null) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(DeparturesListFragment.this.getContext(), "Ocorreu um erro", Toast.LENGTH_SHORT).show();
                    return;
                }

                DepartureListAdapter adapter = new DepartureListAdapter(DeparturesListFragment.this.getContext(), decodedResponse.rows);
                mListView.setAdapter(adapter);

                mLoadingSpinner.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            public void onFailure(Call<RestApiFindDeparturesResponse> call, Throwable t) {
                Log.w("findDeparturesByRouteId", String.valueOf(routeId), t);
                Toast.makeText(DeparturesListFragment.this.getContext(), "Ocorreu um erro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<RestApiFindDeparturesResponse> buildFindDeparturesByRouteIdCall(long routeId) {
        BackendApiClient apiClient = buildBackendApiClient();
        return apiClient.findDeparturesByRouteId(buildAuthorizationString(),
                "staging",
                buildRequestBody("routeId", String.valueOf(routeId)));
    }

    private static class DepartureListAdapter extends BaseAdapter {

        Context context;
        List<DepartureListItem> items;

        public DepartureListAdapter(Context context, List<DepartureListItem> items) {
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
            DepartureListItem item = (DepartureListItem) getItem(position);
            return item.id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.departure_list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.name);

            DepartureListItem item = (DepartureListItem) getItem(position);
            name.setText(item.time);

            return convertView;
        }
    }
}
