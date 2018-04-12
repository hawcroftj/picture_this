package rrc.bit.picturethis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static rrc.bit.picturethis.Main.REQUEST_FIND_PLACE;
import static rrc.bit.picturethis.Main.REQUEST_TAKE_PHOTO;

public class NewPlace extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences prefs;
    private GoogleSignInAccount account;

    private TextView tvPlaceInfo;
    private EditText etTitle, etDescription;
    private ImageView ivPreview;

    // [FeatureName, Thoroughfare, Locality, AdminArea, CountryCode, Lat, Long]
    private ArrayList<String> newPlaceInfo;

    private DatabaseReference databasePlaces;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String photoPath;
    private String newPlacePhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        // initialize preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get "place" and "images" from database
        databasePlaces = db.getReference("place");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        tvPlaceInfo = findViewById(R.id.tvPlaceInfo);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        ivPreview = findViewById(R.id.ivPreview);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnFindPlace = findViewById(R.id.btnFindPlace);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnFindPlace.setOnClickListener(this);
        btnCamera.setOnClickListener(this);

        // get the user account
        Intent intent = getIntent();
        account = intent.getParcelableExtra("account");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnCamera:
                takePhoto();
                break;
            case R.id.btnFindPlace:
                Intent intent = new Intent(this, Map.class);
                intent.putExtra("add_markers", "false");
                startActivityForResult(intent, REQUEST_FIND_PLACE);
                break;
            case R.id.btnSubmit:
                addPlace();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                // show a thumbnail of the image for review before submission
                ivPreview.setImageBitmap(Utility.createBitmapThumbnail(ivPreview, photoPath));
            }
            if (requestCode == REQUEST_FIND_PLACE) {
                newPlaceInfo = data.getStringArrayListExtra("place");
                // show info about the place selected on the map
                displayPlaceInfo();
            }
        }
    }

    private void displayPlaceInfo() {
        if(newPlaceInfo != null) {
            // [FeatureName, Thoroughfare, Locality, AdminArea, CountryCode, Lat, Long]
            tvPlaceInfo.setText(String.format("%s %s %s %s %s", newPlaceInfo.get(0),
                    newPlaceInfo.get(1), newPlaceInfo.get(2),
                    newPlaceInfo.get(3), newPlaceInfo.get(4)));
        }
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

    private void addPlace() {
        // upload place photo first
        Utility.commitPhotoToStorage(storageReference, account.getDisplayName(), photoPath, getApplicationContext());

        //region Firebase json Structure - Visual Representation
//        IDEAL FIREBASE STRUCTURE
//        ====================================
//        places:{
//            place1:{
//                title:
//                latitude:
//                longitude:
//                user:
//                thumb:
//                details:{
//                    description:
//                    streetAddress:
//                    city:
//                    province:
//                    country:
//                }
//                images:{
//                    image1:{
//                        location:
//                        user:
//                    }
//                    ...
//                }
//            }
//            ...
//        }
//        ====================================
        //endregion

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // [0. FeatureName, 1. Thoroughfare, 2. Locality, 3. AdminArea, 4. CountryCode, 5. Lat, 6. Long]
        double latitude = Double.parseDouble(newPlaceInfo.get(5));
        double longitude = Double.parseDouble(newPlaceInfo.get(6));

        String streetNum = newPlaceInfo.get(0);
        String streetName = newPlaceInfo.get(1);
        String city = newPlaceInfo.get(2);
        String province = newPlaceInfo.get(3);
        String country = newPlaceInfo.get(4);

        // create unique primary key
        final String NEW_PLACE_ID = databasePlaces.push().getKey();

        // create new place using information from activity
        // PLACE: placeId, title, user, thumb, latitude, longitude,
        //        description, streetNum, street, city, province, country
        Place newPlace = new Place(NEW_PLACE_ID, title, account.getDisplayName(), newPlacePhotoName, latitude, longitude,
                                   description, streetNum, streetName, city, province, country);

        // add new place to database
        databasePlaces.child(NEW_PLACE_ID).setValue(newPlace);
        Utility.commitPhotoRefToDatabase(databasePlaces, newPlace, account.getDisplayName(), newPlacePhotoName);

        Toast.makeText(this, "Place created.", Toast.LENGTH_SHORT).show();
    }
}
