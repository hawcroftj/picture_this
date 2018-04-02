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
import java.util.ArrayList;

public class PlaceAdapter extends ArrayAdapter<Place> {

    private ArrayList<Place> places;

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
        //ImageView image = convertView.findViewById(R.id.ivImage);

        tvTitle.setText(place.getTitle());
        tvDescription.setText(place.getDetails().getDescription());
        tvUser.setText(place.getUser());

        return convertView;
    }

    @Nullable
    @Override
    public Place getItem(int position) {
        return places.get(position);
    }
}
