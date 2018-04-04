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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Utility {
    private Utility() { }

    public static Bitmap displayThumbnail(ImageView imageView, String photoPath) {
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
        File image = File.createTempFile(imageFileName,".jpg", storageDir);

        //String photoPath = createPhotoPath(image);

        // isolate the new image file name for association in new Place db entry
        //String newPlacePhotoName = createPhotoName(photoPath, imageFileName);

        return image;
    }

    public static String createPhotoPath(File image) {
        return image.getAbsolutePath();
    }

    public static String createPhotoName(String photoPath, String imageFileName) {
        return photoPath.substring(photoPath.indexOf(imageFileName), photoPath.length());
    }

    public static void uploadPicture(StorageReference storageReference, String photoPath, final Context context) {
        Uri file = Uri.fromFile(new File(photoPath));
        StorageReference ref = storageReference.child("images/"+file.getLastPathSegment());
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
                Toast.makeText(context, "Upload failed."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
