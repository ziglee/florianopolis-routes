package net.cassiolandim.florianopolisroutes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.model.RestApiFindDeparturesResponse;
import net.cassiolandim.florianopolisroutes.model.DepartureListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeparturesListFragment extends ParentFragment {

    private long routeId;
    private RecyclerView mListView;
    private RecyclerView.LayoutManager mLayoutManager;
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
        this.mListView = (RecyclerView) rootView.findViewById(android.R.id.list);
        this.mLoadingSpinner = (ProgressBar) rootView.findViewById(android.R.id.progress);
        this.mLayoutManager = new LinearLayoutManager(DeparturesListFragment.this.getContext());
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
                    Toast.makeText(DeparturesListFragment.this.getContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindDeparturesResponse decodedResponse = response.body();
                if (decodedResponse == null) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(DeparturesListFragment.this.getContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, List<String>> map = new HashMap<>();
                for (DepartureListItem item : decodedResponse.rows) {
                    List<String> calendar = map.get(item.calendar);
                    if (calendar == null) {
                        calendar = new ArrayList<>();
                        map.put(item.calendar, calendar);
                    }
                    calendar.add(item.time);
                }

                List<ListItem> list = new ArrayList<>();
                for (String key : map.keySet()) {
                    list.add(new ListItem(key, DepartureListAdapter.HEADER_VIEW));
                    for (String content : map.get(key)) {
                        list.add(new ListItem(content, DepartureListAdapter.REGULAR_VIEW));
                    }
                }

                DepartureListAdapter adapter = new DepartureListAdapter(list);
                mListView.setLayoutManager(mLayoutManager);
                mListView.setAdapter(adapter);

                mLoadingSpinner.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            public void onFailure(Call<RestApiFindDeparturesResponse> call, Throwable t) {
                Log.w("findDeparturesByRouteId", String.valueOf(routeId), t);
                Toast.makeText(DeparturesListFragment.this.getContext(), R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<RestApiFindDeparturesResponse> buildFindDeparturesByRouteIdCall(long routeId) {
        BackendApiClient apiClient = buildBackendApiClient();
        return apiClient.findDeparturesByRouteId(buildAuthorizationString(),
                getString(R.string.backend_api_environment),
                buildRequestBody("routeId", String.valueOf(routeId)));
    }

    private static class DepartureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        public static final int HEADER_VIEW = 0;
        public static final int REGULAR_VIEW = 1;

        private List<ListItem> list;

        public DepartureListAdapter(List<ListItem> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            if (viewType == HEADER_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.departure_list_header, parent, false);
                HeaderViewHolder vh = new HeaderViewHolder(v);
                return vh;
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.departure_list_item, parent, false);
                ItemViewHolder vh = new ItemViewHolder(v);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof HeaderViewHolder) {
                    HeaderViewHolder vh = (HeaderViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof ItemViewHolder) {
                    ItemViewHolder vh = (ItemViewHolder) holder;
                    vh.bindView(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).type;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView nameView;

            public ViewHolder(final View itemView) {
                super(itemView);
                nameView = (TextView) itemView.findViewById(R.id.name);
            }

            public void bindView(int position) {
                nameView.setText(list.get(position).content);
            }
        }

        public class HeaderViewHolder extends ViewHolder {

            public HeaderViewHolder(final View itemView) {
                super(itemView);
            }
        }

        public class ItemViewHolder extends ViewHolder {

            public ItemViewHolder(final View itemView) {
                super(itemView);
            }
        }
    }

    private static class ListItem {

        public String content;
        public int type;

        public ListItem(String content, int type) {
            this.content = content;
            this.type = type;
        }
    }
}
