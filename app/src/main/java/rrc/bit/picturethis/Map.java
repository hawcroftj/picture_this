package rrc.bit.picturethis;

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

        // Add a marker in Winnipeg and move, zoom the camera
        LatLng winnipeg = new LatLng(49.895136, -97.138374);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 12));

        // onMapClick will get the location of a user click
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses = new ArrayList<>();
                Address address = null;

                try{ // try to find an address from the selected location
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                } catch(IOException e){ e.printStackTrace(); }

                try { // if nothing was found, display error message to user
                    address = addresses.get(0);
                } catch(IndexOutOfBoundsException e) {
                    Toast.makeText(Map.this, "No data for selected location.", Toast.LENGTH_SHORT).show();
                }

                if(address != null) {
                    String addressString = getAddressDetails(address);
                    Toast.makeText(Map.this, addressString, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getAddressDetails(Address address) {
        StringBuilder builder = new StringBuilder();

        builder.append(validateAddressData(address.getFeatureName()));
        builder.append(validateAddressData(address.getThoroughfare()));
        builder.append(validateAddressData(address.getLocality()));
        builder.append(validateAddressData(address.getAdminArea()));
        builder.append(validateAddressData(address.getCountryCode()));

        return builder.toString();
    }

    /*
     * Validate data string returned from selected LatLng location.
     */
    private String validateAddressData(String data) {
        try{
            return (data == null || data.isEmpty()) ? "--Unavailable TRY--" : data;
        } catch(IndexOutOfBoundsException e) {
            return "--Unavailable CATCH--";
        }
    }
}
