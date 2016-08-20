package net.cassiolandim.florianopolisroutes.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cassiolandim.florianopolisroutes.BackendApiClient;
import net.cassiolandim.florianopolisroutes.BackendApiUtil;
import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRouteRequest;
import net.cassiolandim.florianopolisroutes.model.RestApiFindRouteResponse;

import org.json.JSONObject;

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

        mListView = (ListView) findViewById(R.id.list_view);

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

        call.enqueue(new Callback<RestApiFindRouteResponse>() {
            @Override
            public void onResponse(Call<RestApiFindRouteResponse> call, Response<RestApiFindRouteResponse> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                RestApiFindRouteResponse decodedResponse = response.body();
                if (decodedResponse == null) {
                    Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // TODO
            }

            public void onFailure(Call<RestApiFindRouteResponse> call, Throwable t) {
                Log.w("findRoutesByStopName", streetName, t);

                Snackbar.make(mListView, "Ocorreu um erro", Snackbar.LENGTH_SHORT).show();
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
}
