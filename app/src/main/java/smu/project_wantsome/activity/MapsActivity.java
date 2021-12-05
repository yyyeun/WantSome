package smu.project_wantsome.activity;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import smu.project_wantsome.R;
import smu.project_wantsome.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private double lat = 37.541;
    private double lon = 126.986;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        location = getIntent().getStringExtra("location");
        Log.e("location", ""+location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Geocoder g = new Geocoder(MapsActivity.this);
        List<Address> address = null;
        try {
            address = g.getFromLocationName(location, 2);
        } catch (IOException e) {
            e.printStackTrace();
            location = "서울";
            Log.d("test", "입출력오류");
        }
        if (address != null) {
            if (address.size() == 0) {
                Log.e("주소찾기 오류", "");
                location = "서울";
            } else {
                Log.d("찾은 주소", address.get(0).toString());
                lat = address.get(0).getLatitude();
                lon = address.get(0).getLongitude();
            }
        } else location = "서울";
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in location and move the camera
        LatLng sellerLocation = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sellerLocation).title(location)).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sellerLocation));

        // Zoom 기능 구현
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        // Zoom Controller 추가
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}