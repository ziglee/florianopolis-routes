package net.cassiolandim.florianopolisroutes.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.backend.BackendApiUtil;
import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.model.FindRouteListItem;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRouteRequest;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRouteResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private ProgressBar mLoadingSpinner;

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.MINUTES).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        mListView = (ListView) findViewById(R.id.list_view);

        mLoadingSpinner.setVisibility(View.GONE);

        final AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // TODO
            }
        };

        mListView.setOnItemClickListener(mMessageClickedHandler);

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.street_name);
                doSearch(editText.getText().toString());
            }
        });
    }

    private void doSearch(final String streetName) {
        Call<RestApiFindRouteResponse> call = buildFindRoutesByStopNameCall(streetName);

        mLoadingSpinner.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);

        call.enqueue(new Callback<RestApiFindRouteResponse>() {
            @Override
            public void onResponse(Call<RestApiFindRouteResponse> call, Response<RestApiFindRouteResponse> response) {
                if (!response.isSuccessful()) {
                    mLoadingSpinner.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindRouteResponse decodedResponse = response.body();
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

            public void onFailure(Call<RestApiFindRouteResponse> call, Throwable t) {
                Log.w("findRoutesByStopName", streetName, t);
                Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();

                mLoadingSpinner.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        });
    }

    private Call<RestApiFindRouteResponse> buildFindRoutesByStopNameCall(String streetName) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.backend_api_base_url))
                .client(okHttpClient)
                .build();

        String username = getString(R.string.backend_api_username);
        String password = getString(R.string.backend_api_password);
        String authorization = BackendApiUtil.authorizationHeaderValue(username, password);

        RestApiFindRouteRequest params = new RestApiFindRouteRequest("stopName", "%" + streetName + "%");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(params));
        BackendApiClient apiClient = retrofit.create(BackendApiClient.class);
        return apiClient.findRoutesByStopName(authorization, "staging", reqBody);
    }

    private static class RouteListAdapter extends BaseAdapter {

        Context context;
        List<FindRouteListItem> items;

        public RouteListAdapter(Context context, List<FindRouteListItem> items) {
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
            FindRouteListItem item = (FindRouteListItem) getItem(position);
            return item.id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.route_list_item, parent, false);
            }

            TextView shortName = (TextView) convertView.findViewById(R.id.short_name);
            TextView longName = (TextView) convertView.findViewById(R.id.long_name);

            FindRouteListItem item = (FindRouteListItem) getItem(position);
            shortName.setText(item.shortName);
            longName.setText(item.longName);

            return convertView;
        }
    }
}
