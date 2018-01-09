package com.androidtutorialpoint.glogin;

/**
 * Created by hoangducloc on 12/1/17.
 */

public class ImageUploadInfo {
    public String imageName;

    public String imageURL;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url) {

        this.imageName = name;
        this.imageURL= url;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }
}
