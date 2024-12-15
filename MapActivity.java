package com.iasonas.melionis;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    public MapView myMapView;
    public LatLng myLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent Intent = getIntent();
        String data = Intent.getStringExtra("data");
        myLoc = getLocation(data);

        myMapView = (MapView) findViewById(R.id.mapView);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        myMap.addMarker(new MarkerOptions()
                .position(myLoc)
                .title("Location"));
    }

    public LatLng getLocation(String data) {

        String[] location = data.split("/");
        double La = Double.parseDouble(location[0]);
        double Lo = Double.parseDouble(location[1]);
        LatLng loc = new LatLng(La, Lo);

        return loc;
    }
}
