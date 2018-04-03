package rrc.bit.picturethis;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;

public class ShowPlace extends AppCompatActivity {

    private Place place;
    private ImageView ivPlaceImage, ivNewImage;
    private TextView tvTitle, tvDescription, tvUser, tvAddress, tvLatLong, tvCreated;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = firebaseStorage.getReference();

    private final int PICK_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ivPlaceImage = findViewById(R.id.ivPlaceImage);
        ivNewImage = findViewById(R.id.ivNewImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress = findViewById(R.id.tvAddress);
        tvLatLong = findViewById(R.id.tvLatLong);
        tvUser = findViewById(R.id.tvUser);
        tvCreated = findViewById(R.id.tvCreated);

        Button btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                final Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch(IOException e){}
                //use the bitmap as you like
                ivNewImage.setImageBitmap(bitmap);
            }
        }
    }
}
