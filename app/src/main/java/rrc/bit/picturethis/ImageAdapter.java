package rrc.bit.picturethis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Image> {

    private ArrayList<Image> images;
    private ImageView ivListImage;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference databasePlaces = db.getReference("place");

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = firebaseStorage.getReference();

    public ImageAdapter(Context context, ArrayList<Image> images) {
        super(context, 0, images);
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Image image = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.image_row, parent, false);
        }

        ivListImage = convertView.findViewById(R.id.ivListImage);
        TextView tvImageUser = convertView.findViewById(R.id.tvImageUser);

        tvImageUser.setText(image.getUser());

        StorageReference imageRef = storageRef.child("images/" + image.getImage());

        // load image from FireBase using Glide
        Glide.with(this.getContext())
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .override(500, 500)
                .into(ivListImage);

        return convertView;
    }
}