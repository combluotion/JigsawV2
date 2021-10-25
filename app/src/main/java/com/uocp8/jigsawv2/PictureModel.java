package com.uocp8.jigsawv2;

public class PictureModel {
    private String picture;
    private int imgPicture;

    public PictureModel() {
    }

    public PictureModel(String picture, int imgPicture) {
        this.picture = picture;
        this.imgPicture = imgPicture;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getImgPicture() {
        return imgPicture;
    }

    public void setImgPicture(int imgPicture) {
        this.imgPicture = imgPicture;
    }
}
