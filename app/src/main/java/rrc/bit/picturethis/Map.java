package rrc.bit.picturethis;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Hashtable;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initialize geocoder with default locale
        geocoder = new Geocoder(Map.this, Locale.getDefault());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Winnipeg and move, zoom the camera
        LatLng winnipeg = new LatLng(49.895136, -97.138374);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 12));

        // onMapClick will get the location of a user click
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> places = new ArrayList<>();
                Address place = null;

                try{ // try to find an address from the selected location
                    places = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                } catch(IOException e){ e.printStackTrace(); }

                try { // if nothing was found, display error message to user
                    place = places.get(0);
                } catch(IndexOutOfBoundsException e) {
                    Toast.makeText(Map.this, "No data for selected location.", Toast.LENGTH_SHORT).show();
                }

                if(place != null) {
                    Hashtable placeInfo = getAddressDetails(place);
                    Intent returnPlace = new Intent();
                    returnPlace.putExtra("place", placeInfo);
                    setResult(Activity.RESULT_OK, returnPlace);
                    finish();
                }
            }
        });

    }

    private Hashtable getAddressDetails(Address place) {
        Hashtable<String, String> placeInfo = new Hashtable<>();

        placeInfo.put("streetNum", validateAddressData(place.getFeatureName()));
        placeInfo.put("street", validateAddressData(place.getThoroughfare()));
        placeInfo.put("city", validateAddressData(place.getLocality()));
        placeInfo.put("province", validateAddressData(place.getAdminArea()));
        placeInfo.put("country", validateAddressData(place.getCountryCode()));

        return placeInfo;
    }

    /*
     * Validate data string returned from selected LatLng location.
     */
    private String validateAddressData(String data) {
        return (data == null || data.isEmpty()) ? "Unavailable" : data;
    }
}
