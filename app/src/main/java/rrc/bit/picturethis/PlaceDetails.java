package rrc.bit.picturethis;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class PlaceDetails implements Parcelable{

    private String description, streetNum, street, city, province, country;
    private Date created;

    public PlaceDetails() { }

    public PlaceDetails(String description, String streetNum, String street,
                        String city, String province, String country) {
        this.description = description;
        this.streetNum = streetNum;
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.created = Calendar.getInstance().getTime();
    }

    protected PlaceDetails(Parcel in) {
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
        dest.writeString(description);
        dest.writeString(streetNum);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeLong(created != null ? created.getTime() : -1L);
    }

    public static final Parcelable.Creator<PlaceDetails> CREATOR = new Parcelable.Creator<PlaceDetails>() {
        @Override
        public PlaceDetails createFromParcel(Parcel in) {
            return new PlaceDetails(in);
        }

        @Override
        public PlaceDetails[] newArray(int size) {
            return new PlaceDetails[size];
        }
    };

    public String getDescription() { return description; }

    public String getStreetNum() { return streetNum; }

    public String getStreet() { return street; }

    public String getCity() { return city; }

    public String getProvince() { return province; }

    public String getCountry() { return country; }

    public Date getCreated() { return created; }
}
