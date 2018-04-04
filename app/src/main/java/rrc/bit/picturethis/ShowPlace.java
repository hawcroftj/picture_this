package rrc.bit.picturethis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static rrc.bit.picturethis.Main.REQUEST_PICK_IMAGE;
import static rrc.bit.picturethis.Main.REQUEST_TAKE_PHOTO;

public class ShowPlace extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences prefs;
    private GoogleSignInAccount account;

    private Place place;
    private ImageView ivPlaceImage, ivNewImage;
    private TextView tvTitle, tvDescription, tvUser, tvAddress, tvLatLong, tvCreated;

    private DatabaseReference databasePlaces;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = firebaseStorage.getReference();

    private Uri bitmapUri;
    private String photoPath;
    private String newPlacePhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ivPlaceImage = findViewById(R.id.ivPlaceImage);
        ivNewImage = findViewById(R.id.ivNewImage);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress = findViewById(R.id.tvAddress);
        tvLatLong = findViewById(R.id.tvLatLong);
        tvUser = findViewById(R.id.tvUser);
        tvCreated = findViewById(R.id.tvCreated);

        Button btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnUploadPhoto.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        // get the Place to show, and the user account
        Intent intent = getIntent();
        place = intent.getParcelableExtra("place");
        account = intent.getParcelableExtra("account");
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

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure the app can handle a camera request
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                // create photo file and store path, name
                photoFile = Utility.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                photoPath = Utility.createPhotoPath(photoFile);
                newPlacePhotoName = Utility.createPhotoName(photoPath, photoFile.getName());
            } catch (IOException e) { }
            // if the File was created successfully
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "rrc.bit.picturethis", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void uploadPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnUploadPhoto:
                uploadPhoto();
                break;
            case R.id.btnTakePhoto:
                takePhoto();
                // null bitmap disables bitmap upload to FireBase
                // instead, photo at photoPath will be uploaded
                bitmapUri = null;
                break;
            case R.id.btnSubmit:
                Utility.commitPhotoToStorage(storageRef, photoPath, bitmapUri, getApplicationContext());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                // set the thumbnail to the image returned from storage
                bitmapUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), bitmapUri);
                    ivNewImage.setImageBitmap(bitmap);

                    // create photo file and store path, name
                    File photoFile = Utility.createImageFile();
                    photoPath = Utility.createPhotoPath(photoFile);
                    newPlacePhotoName = Utility.createPhotoName(photoPath, photoFile.getName());
                } catch(IOException e) { }
            }
            if(requestCode == REQUEST_TAKE_PHOTO) {
                // set the thumbnail to the image returned from camera
                ivNewImage.setImageBitmap(Utility.createBitmapThumbnail(ivNewImage, photoPath));
            }
        }
    }
}
