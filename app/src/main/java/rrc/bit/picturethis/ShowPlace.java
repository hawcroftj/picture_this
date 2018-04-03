package rrc.bit.picturethis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowPlace extends AppCompatActivity {

    private Place place;
    private ImageView ivPlaceImage;
    private TextView tvTitle, tvDescription, tvUser, tvAddress, tvLatLong, tvCreated;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = firebaseStorage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        ivPlaceImage = findViewById(R.id.ivPlaceImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress = findViewById(R.id.tvAddress);
        tvLatLong = findViewById(R.id.tvLatLong);
        tvUser = findViewById(R.id.tvUser);
        tvCreated = findViewById(R.id.tvCreated);

        place = getIntent().getExtras().getParcelable("place");
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvTitle.setText(place.getTitle());
        tvDescription.setText(place.getDescription());
        tvUser.setText(String.format("Submitted by: %s", place.getUser()));
        tvCreated.setText(place.getCreated().toString());
        tvLatLong.setText(String.format("Lat: %f, Long: %f", place.getLatitude(), place.getLongitude()));
        tvAddress.setText(String.format("%s %s, %s, %s %s", place.getStreetNum(), place.getStreet(), place.getCity(), place.getProvince(), place.getCountry()));

        StorageReference image = storageRef.child("images/" + place.getThumb());

        // load image from FireBase using Glide
        Glide.with(ShowPlace.this)
                .using(new FirebaseImageLoader())
                .load(image)
                .centerCrop()
                .fitCenter()
                .into(ivPlaceImage);
    }
}
