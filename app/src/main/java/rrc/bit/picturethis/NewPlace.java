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
import java.util.Date;
import java.util.UUID;

public class NewPlace extends AppCompatActivity implements View.OnClickListener{

    EditText etTitle, etDescription;
    ImageView ivPreview;
    Button btnCamera, btnSubmit;

    private DatabaseReference databasePlaces;
    private DatabaseReference databaseUsers;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private GoogleSignInAccount account;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        FirebaseApp.initializeApp(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // get "place" and "users" from database
        databasePlaces = db.getReference("place");
        databaseUsers = db.getReference("users");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        ivPreview = findViewById(R.id.ivPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnCamera.setOnClickListener(this);

        Intent intent = getIntent();
        account = intent.getParcelableExtra("account");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnCamera:
                takePicture();
                break;
            case R.id.btnSubmit:
                addPlace(databasePlaces);
                uploadPicture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // show a thumbnail of the image for review before submission
        displayThumbnail();
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

    private void addPlace(DatabaseReference database) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // create unique primary key
        String id = database.push().getKey();
        // create new place using information from activity
        Place newPlace = new Place(id, title, description, account.getDisplayName());
        // add new place to database
        database.child(id).setValue(newPlace);

        Toast.makeText(this, "Place created.", Toast.LENGTH_SHORT).show();
    }
}
