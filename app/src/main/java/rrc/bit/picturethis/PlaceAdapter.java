package rrc.bit.picturethis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PlaceAdapter extends ArrayAdapter<Place> {

    private ArrayList<Place> places;
    private ImageView ivImage;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef = firebaseStorage.getReference();

    public PlaceAdapter(Context context, ArrayList<Place> places) {
        super(context, 0, places);
        this.places = places;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Place place = getItem(position);

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.place_row, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        TextView tvUser = convertView.findViewById(R.id.tvUser);
        ivImage = convertView.findViewById(R.id.ivImage);

        tvTitle.setText(place.getTitle());
        tvDescription.setText(place.getDescription());
        tvUser.setText(place.getUser());

        StorageReference image = storageRef.child("images/" + place.getThumb());

        // load image from FireBase using Glide
        Glide.with(this.getContext())
                .using(new FirebaseImageLoader())
                .load(image)
                .override(100, 100)
                .into(ivImage);

        return convertView;
    }
}
