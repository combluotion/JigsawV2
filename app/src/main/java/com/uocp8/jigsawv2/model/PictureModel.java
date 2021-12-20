package com.uocp8.jigsawv2.model;

import android.graphics.Bitmap;

public class PictureModel {
    private String picture;
    private int imgPicture;
    private Bitmap fireBasePicture;

    public PictureModel() {
    }

    public PictureModel(String picture, int imgPicture) {
        this.picture = picture;
        this.imgPicture = imgPicture;
        this.fireBasePicture = null;
    }
    public PictureModel(String picture, Bitmap fireBasePicture){
        this.picture = picture;
        this.imgPicture = 0;
        this.fireBasePicture = fireBasePicture;
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

    public Bitmap getFireBasePicture() {
        return fireBasePicture;
    }

    public void setFireBasePicture(Bitmap fireBasePicture) {
        this.fireBasePicture = fireBasePicture;
    }
}
