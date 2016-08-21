package net.cassiolandim.florianopolisroutes.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.model.RouteListItem;
import net.cassiolandim.florianopolisroutes.model.RestApiRequest;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRoutesResponse;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends ParentActivity {

    public static final int PICK_STREET_NAME = 0;

    private EditText mStreetNameEditText;
    private ListView mListView;
    private ProgressBar mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mListView = (ListView) findViewById(R.id.list_view);
        mStreetNameEditText = (EditText) findViewById(R.id.street_name);

        mLoadingSpinner.setVisibility(View.GONE);

        final AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                RouteListItem item = (RouteListItem) parent.getAdapter().getItem(position);
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EXTRA_ID, item.id);
                intent.putExtra(DetailsActivity.EXTRAS_NAME, item.shortName + " - " + item.longName);
                startActivity(intent);
            }
        };

        mListView.setOnItemClickListener(mMessageClickedHandler);

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch(mStreetNameEditText.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            Intent intent = new Intent(ListActivity.this, MapsActivity.class);
            startActivityForResult(intent, PICK_STREET_NAME);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_STREET_NAME) {
            if (resultCode == RESULT_OK) {
                String streetName = data.getStringExtra(MapsActivity.EXTRA_STREET_NAME);
                mStreetNameEditText.setText(streetName);
                doSearch(streetName);
            }
        }
    }

    private void doSearch(final String streetName) {
        Call<RestApiFindRoutesResponse> call = buildFindRoutesByStopNameCall(streetName);

        mLoadingSpinner.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);

        call.enqueue(new Callback<RestApiFindRoutesResponse>() {
            @Override
            public void onResponse(Call<RestApiFindRoutesResponse> call, Response<RestApiFindRoutesResponse> response) {
                if (!response.isSuccessful()) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindRoutesResponse decodedResponse = response.body();
                if (decodedResponse == null) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                RouteListAdapter adapter = new RouteListAdapter(ListActivity.this, decodedResponse.rows);
                mListView.setAdapter(adapter);

                mLoadingSpinner.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            public void onFailure(Call<RestApiFindRoutesResponse> call, Throwable t) {
                Log.w("findRoutesByStopName", streetName, t);
                Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();

                mLoadingSpinner.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        });
    }

    private Call<RestApiFindRoutesResponse> buildFindRoutesByStopNameCall(String streetName) {
        BackendApiClient apiClient = buildBackendApiClient();
        String authorization = buildAuthorizationString();

        RestApiRequest params = new RestApiRequest("stopName", "%" + streetName + "%");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(params));
        return apiClient.findRoutesByStopName(authorization, "staging", reqBody);
    }

    private static class RouteListAdapter extends BaseAdapter {

        Context context;
        List<RouteListItem> items;

        public RouteListAdapter(Context context, List<RouteListItem> items) {
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
            RouteListItem item = (RouteListItem) getItem(position);
            return item.id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.route_list_item, parent, false);
            }

            TextView shortName = (TextView) convertView.findViewById(R.id.short_name);
            TextView longName = (TextView) convertView.findViewById(R.id.long_name);

            RouteListItem item = (RouteListItem) getItem(position);
            shortName.setText(item.shortName);
            longName.setText(item.longName);

            return convertView;
        }
    }
}
