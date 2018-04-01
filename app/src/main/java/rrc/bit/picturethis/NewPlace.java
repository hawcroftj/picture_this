package rrc.bit.picturethis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

public class NewPlace extends AppCompatActivity implements View.OnClickListener{

    private TextView tvPlaceInfo;
    private EditText etTitle, etDescription;
    private ImageView ivPreview;
    private Button btnCamera, btnSubmit, btnFindPlace;

    // [FeatureName, Thoroughfare, Locality, AdminArea, CountryCode, Lat, Long]
    private ArrayList<String> newPlaceInfo;

    private DatabaseReference databasePlaces;
    private DatabaseReference databaseUsers;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private GoogleSignInAccount account;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_FIND_PLACE = 2;
    private String photoPath;
    private String newPlacePhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get "place" and "images" from database
        databasePlaces = db.getReference("place");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        tvPlaceInfo = findViewById(R.id.tvPlaceInfo);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        ivPreview = findViewById(R.id.ivPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnFindPlace = findViewById(R.id.btnFindPlace);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnFindPlace.setOnClickListener(this);
        btnCamera.setOnClickListener(this);

        Intent intent = getIntent();
        account = intent.getParcelableExtra("account");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnCamera:
                takePicture();
                break;
            case R.id.btnFindPlace:
                Intent intent = new Intent(this, Map.class);
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
                displayThumbnail();
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

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure the app can handle a camera request
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) { }
            // if the File was created successfully
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "rrc.bit.picturethis", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // create the image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        photoPath = image.getAbsolutePath();

        // isolate the new image file name for association in new Place db entry
        newPlacePhotoName = photoPath.substring(photoPath.indexOf(imageFileName), photoPath.length());

        return image;
    }

    private void displayThumbnail() {
        // get dimensions of the ImageView
        int targetW = ivPreview.getWidth();
        int targetH = ivPreview.getHeight();

        // get the dimensions of the image taken
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // calculate a scale factor
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // decode the image to a bitmap size that fits in the ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        ivPreview.setImageBitmap(bitmap);
    }

    private void uploadPicture() {
        Uri file = Uri.fromFile(new File(photoPath));
        StorageReference ref = storageReference.child("images/"+file.getLastPathSegment());
        ref.putFile(file).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(NewPlace.this, "progress: " + taskSnapshot.getBytesTransferred(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(NewPlace.this, "Upload successful.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewPlace.this, "Upload failed."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPlace() {
        // upload place photo first
        uploadPicture();

        //region Firebase json Structure - Visual Representation
//        FIREBASE STRUCTURE
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
        Place newPlace = new Place(NEW_PLACE_ID, title, description, account.getDisplayName());

        // add new place to database
        databasePlaces.child(NEW_PLACE_ID).setValue(newPlace);

        Toast.makeText(this, "Place created.", Toast.LENGTH_SHORT).show();
    }
}
