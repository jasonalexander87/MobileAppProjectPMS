package com.iasonas.melionis;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GetLocationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private LocationManager mLocationManager;

    public GetLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ResultReceiver rec = intent.getParcelableExtra("listener");

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double La = loc.getLatitude();
        double Lo = loc.getLongitude();
        String LaS = String.valueOf(La);
        String LoS = String.valueOf(Lo);

        String result = LaS + "/" + LoS;

        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        // Here we call send passing a resultCode and the bundle of extras
        rec.send(Activity.RESULT_OK, bundle);

    }

}

