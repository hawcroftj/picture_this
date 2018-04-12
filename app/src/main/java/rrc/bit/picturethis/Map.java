package rrc.bit.picturethis;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;

    private DatabaseReference databasePlaces;

    private boolean addMarkers;
    ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initialize geocoder with default locale
        geocoder = new Geocoder(Map.this, Locale.getDefault());

        // receive intent to determine whether markers should be loaded
        Intent intent = getIntent();
        addMarkers = intent.getStringExtra("add_markers").equals("true");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // add a marker in Winnipeg and move, zoom the camera
        LatLng winnipeg = new LatLng(49.895136, -97.138374);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(winnipeg, 12));

        // if Map was launched from Main, add markers to view existing Places
        // if Map was launched from NewPlace, prepare for user to select a new Place
        if(addMarkers) {
            markers = new ArrayList<>();
            // prepare database connection
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            databasePlaces = db.getReference("place");

            databasePlaces.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        // grab each Place from the snapshot and add it to the Map as a marker
                        Place place = childSnapshot.getValue(Place.class);
                        LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(placeLatLng)
                                .title(place.getTitle())
                                .snippet(place.getDescription())
                                .alpha(0.75f));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });

        } else {
            // onMapClick will get the location of a user click
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    List<Address> places = new ArrayList<>();
                    Address place = null;

                    try { // try to find an address from the selected location
                        places = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try { // if nothing was found, display error message to user
                        place = places.get(0);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(Map.this, "No data for selected location.", Toast.LENGTH_SHORT).show();
                    }

                    if (place != null) {
                        ArrayList<String> placeInfo = getAddressDetails(place);
                        Intent returnPlace = new Intent();
                        returnPlace.putStringArrayListExtra("place", placeInfo);
                        setResult(Activity.RESULT_OK, returnPlace);
                        finish();
                    }
                }
            });
        }

    }

    private ArrayList<String> getAddressDetails(Address place) {
        ArrayList<String> placeInfo = new ArrayList<>();

        placeInfo.add(validateAddressData(place.getFeatureName()));     // FeatureName  | Street Num
        placeInfo.add(validateAddressData(place.getThoroughfare()));    // Thoroughfare | Street Name
        placeInfo.add(validateAddressData(place.getLocality()));        // Locality     | City
        placeInfo.add(validateAddressData(place.getAdminArea()));       // AdminArea    | Province / State
        placeInfo.add(validateAddressData(place.getCountryCode()));     // CountryCode  | Country Code
        placeInfo.add(String.valueOf(place.getLatitude()));             // Latitude     | Lat Value
        placeInfo.add(String.valueOf(place.getLongitude()));            // Longitude    | Long Value

        return placeInfo;
    }

    /*
     * Validate data string returned from selected LatLng location.
     */
    private String validateAddressData(String data) {
        return (data == null || data.isEmpty()) ? "Unavailable" : data;
    }
}
