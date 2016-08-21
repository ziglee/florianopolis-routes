package net.cassiolandim.florianopolisroutes.activity;

import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.backend.BackendApiClient;
import net.cassiolandim.florianopolisroutes.backend.BackendApiUtil;
import net.cassiolandim.florianopolisroutes.model.RestApiRequest;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ParentActivity extends AppCompatActivity {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(2, TimeUnit.MINUTES).build();

    String buildAuthorizationString() {
        String username = getString(R.string.backend_api_username);
        String password = getString(R.string.backend_api_password);
        return BackendApiUtil.authorizationHeaderValue(username, password);
    }

    RequestBody buildRequestBody(String key, String value) {
        RestApiRequest params = new RestApiRequest(key, value);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return RequestBody.create(MediaType.parse("application/json"), gson.toJson(params));
    }

    BackendApiClient buildBackendApiClient() {
        Retrofit retrofit =  new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.backend_api_base_url))
                .client(okHttpClient)
                .build();
        return retrofit.create(BackendApiClient.class);
    }
}
