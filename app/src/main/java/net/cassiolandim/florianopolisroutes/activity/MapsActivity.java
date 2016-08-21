package net.cassiolandim.florianopolisroutes.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.cassiolandim.florianopolisroutes.R;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_STREET_NAME = "EXTRA_STREET_NAME";

    private LatLng lastLatLng = new LatLng(-27.6233845,-48.4911065);

    private GoogleMap mMap;
    private ProgressBar mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TranslateLatLngToStreetTask().execute(lastLatLng);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 11));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);

                lastLatLng = latLng;
            }
        });
    }

    private class TranslateLatLngToStreetTask extends AsyncTask<LatLng, Void, String> {

        @Override
        protected void onPreExecute() {
            mLoadingSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);

            LatLng latLng = params[0];

            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);

                    if (address.getThoroughfare() != null)
                        return address.getThoroughfare();

                    if (address.getMaxAddressLineIndex() == -1)
                        return null;

                    return address.getAddressLine(0);
                }
                return null;
            } catch (IOException ioe) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String streetName) {
            mLoadingSpinner.setVisibility(View.GONE);

            if (streetName != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_STREET_NAME, streetName);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(MapsActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
