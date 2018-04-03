package rrc.bit.picturethis;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class Place implements Parcelable{

    private String placeId, title, user, thumb;                             // info
    private double latitude, longitude;
    private String description, streetNum, street, city, province, country; // details
    private Date created;

    public Place() { }

    public Place(String placeId, String title, String user, String thumb, double latitude, double longitude,
                 String description, String streetNum, String street, String city, String province, String country) {
        // primary Place info
        this.placeId = placeId;
        this.title = title;
        this.user = user;
        this.thumb = thumb;
        this.latitude = latitude;
        this.longitude = longitude;
        // additional details
        this.description = description;
        this.streetNum = streetNum;
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.created = Calendar.getInstance().getTime();
    }

    protected Place(Parcel in) {
        placeId = in.readString();
        title = in.readString();
        user = in.readString();
        thumb = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();

        description = in.readString();
        streetNum = in.readString();
        street = in.readString();
        city = in.readString();
        country = in.readString();
        long tmpCreated = in.readLong();
        created = tmpCreated != -1 ? new Date(tmpCreated) : null;
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

        dest.writeString(description);
        dest.writeString(streetNum);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeLong(created != null ? created.getTime() : -1L);
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

    // primary info
    public String getPlaceId() { return placeId; }

    public String getTitle() { return title; }

    public String getUser() { return user; }

    public String getThumb() { return thumb; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    // details
    public String getDescription() { return description; }

    public String getStreetNum() { return streetNum; }

    public String getStreet() { return street; }

    public String getCity() { return city; }

    public String getProvince() { return province; }

    public String getCountry() { return country; }

    public Date getCreated() { return created; }
}
