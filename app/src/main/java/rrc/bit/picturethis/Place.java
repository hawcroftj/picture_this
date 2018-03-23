package rrc.bit.picturethis;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class Place implements Parcelable{
    private String placeId;
    private String title;
    private String description;
    private String user;
    private Date created;
    private Date updated;

    public Place() { }

    public Place(String placeId, String title, String description, String user) {
        this.placeId = placeId;
        this.title = title;
        this.description = description;
        this.user = user;
        this.created = Calendar.getInstance().getTime();
        this.updated = Calendar.getInstance().getTime();
    }

    protected Place(Parcel in) {
        placeId = in.readString();
        title = in.readString();
        description = in.readString();
        user = in.readString();
        long tmpCreated = in.readLong();
        created = tmpCreated != -1 ? new Date(tmpCreated) : null;
        long tmpUpdated = in.readLong();
        updated = tmpUpdated != -1 ? new Date(tmpUpdated) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(user);
        dest.writeLong(created != null ? created.getTime() : -1L);
        dest.writeLong(updated != null ? updated.getTime() : -1L);
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

    public String getUser() {
        return user;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }
}
