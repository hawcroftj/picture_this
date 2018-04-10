package rrc.bit.picturethis;

public class Image {
    private String imageId;
    private String image;
    private String user;

    public Image() { }

    public Image(String imageId, String image, String user) {
        this.imageId = imageId;
        this.image = image;
        this.user = user;
    }

    public String getImageId() { return imageId; }

    public String getImage() { return image; }

    public String getUser() { return user; }
}
