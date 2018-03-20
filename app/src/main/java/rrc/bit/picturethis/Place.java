package rrc.bit.picturethis;

import java.util.Calendar;
import java.util.Date;

public class Place {
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
