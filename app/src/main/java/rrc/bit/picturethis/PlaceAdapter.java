package rrc.bit.picturethis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>{
    private List<Place> places;

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        // a View is defined for each piece of data to be displayed in the row
        private TextView title, description, user;
        //private ImageView image;

        public PlaceViewHolder(View view) {
            super(view);
            // initialize Views from the place_row.xml layout file
            this.title = view.findViewById(R.id.tvTitle);
            this.description = view.findViewById(R.id.tvDescription);
            this.user = view.findViewById(R.id.tvUser);
            //this.image = view.findViewById(R.id.ivImage);
        }
    }

    // initializes list of FeedItems for use in this adapter
    public PlaceAdapter(List<Place> places) {
        this.places = places;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the place_row and pass it to a new ViewHolder object
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row, parent, false);

        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.title.setText(place.getTitle());
        holder.description.setText(place.getDescription());
        holder.user.setText(place.getUser());
        //holder.image.setImageResource(item.getImage());
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}
