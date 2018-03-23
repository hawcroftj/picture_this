package rrc.bit.picturethis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListPlace extends AppCompatActivity {

    private ArrayList<Place> places = new ArrayList<>();
    private PlaceAdapter adapter;
    private ListView lvPlaces;

    private DatabaseReference databasePlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_place);

        places = new ArrayList<>();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        // get "place" from database
        databasePlaces = db.getReference("place");

        // initialize RecyclerView and set layout defaults
        lvPlaces = findViewById(R.id.lvPlaces);

        adapter = new PlaceAdapter(this, places);
        lvPlaces.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databasePlaces.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                places.clear();
                for(DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    Place place = placeSnapshot.getValue(Place.class);
                    places.add(place);
                }

                PlaceAdapter adapter = new PlaceAdapter(getApplicationContext(), places);
                lvPlaces.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
