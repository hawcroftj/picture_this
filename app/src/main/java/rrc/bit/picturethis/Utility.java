package rrc.bit.picturethis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public final class Utility {
    private Utility() { }

    public static Bitmap createBitmapThumbnail(ImageView imageView, String photoPath) {
        // get dimensions of the ImageView
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

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
        return bitmap;
    }

    public static File createImageFile(File storageDir) throws IOException {
        // create the image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // if the images comes from the camera, save it in the gallery
        // if the image is chosen from the gallery, save it to a temporary location for upload
        return (storageDir != null) ?
                File.createTempFile(imageFileName,".jpg", storageDir) :
                File.createTempFile(imageFileName,".jpg");
    }

    public static File createImageFile() throws  IOException {
        return createImageFile(null);
    }

    public static String createPhotoPath(File image) { return image.getAbsolutePath(); }

    public static String createPhotoName(String photoPath, String imageFileName) {
        return photoPath.substring(photoPath.indexOf(imageFileName), photoPath.length());
    }

    public static void commitPhotoToStorage(StorageReference storageReference, final DatabaseReference databasePlace,
                                            String accountName, Place place, String photoPath, Uri bitmap, final Context context) {
        String fileName;
        Uri file;
        StorageReference ref;

        if(bitmap == null) { // if a photo was taken with camera and saved to the gallery
            file = Uri.fromFile(new File(photoPath));         // get photo from its location
            fileName = file.getLastPathSegment();
            ref = storageReference.child("images/" + fileName);
        } else {            // if a photo was chosen from a storage location
            file = bitmap;                                    // ignore temp file path, use image from device storage
            Uri filePath = Uri.fromFile(new File(photoPath)); // get file name, ignore file at this location
            fileName = filePath.getLastPathSegment();
            ref = storageReference.child("images/" + fileName);
        }

        // add a reference to this image in the Place database entry
        commitPhotoRefToDatabase(databasePlace, place, accountName, fileName);

        ref.putFile(file).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "progress: " + taskSnapshot.getBytesTransferred(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(context, "Upload successful.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Upload failed." + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void commitPhotoToStorage(StorageReference storageReference, String accountName,
                                            String photoPath, final Context context) {
        commitPhotoToStorage(storageReference, null, accountName, null, photoPath, null, context);
    }

    public static void commitPhotoRefToDatabase(DatabaseReference databasePlace, Place place, String accountName, String fileName) {
        if(databasePlace != null && place != null) {
            // create unique primary key
            final String NEW_IMAGE_ID = databasePlace.child(place.getPlaceId()).child("images").push().getKey();

            Image image = new Image(NEW_IMAGE_ID, fileName, accountName);

            // add new image to place in database
            DatabaseReference imageRef = databasePlace.child(place.getPlaceId()).child("images").child(NEW_IMAGE_ID);
            imageRef.setValue(image);
        }
    }
}
