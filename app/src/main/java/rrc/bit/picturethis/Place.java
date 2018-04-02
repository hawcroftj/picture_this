package rrc.bit.picturethis;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class Place implements Parcelable{

    private String placeId, title, user, thumb;
    private double latitude, longitude;
    private PlaceDetails details;

    public Place() { }

    public Place(String placeId, String title, String user, String thumb,
                 double latitude, double longitude, PlaceDetails details) {
        this.placeId = placeId;
        this.title = title;
        this.user = user;
        this.thumb = thumb;
        this.latitude = latitude;
        this.longitude = longitude;
        this.details = details;
    }

    protected Place(Parcel in) {
        placeId = in.readString();
        title = in.readString();
        user = in.readString();
        thumb = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(title);
        dest.writeString(user);
        dest.writeString(thumb);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public String getPlaceId() { return placeId; }

    public String getTitle() { return title; }

    public String getUser() { return user; }

    public String getThumb() { return thumb; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public PlaceDetails getDetails() { return details; }
}
