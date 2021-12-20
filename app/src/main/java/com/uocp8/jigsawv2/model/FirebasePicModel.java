package com.uocp8.jigsawv2.model;

import android.graphics.Bitmap;

public class FirebasePicModel {
    private String picture;
    private Bitmap fireBasePicture;

    public FirebasePicModel(String picture, Bitmap fireBasePicture){
        this.picture = picture;
        this.fireBasePicture = fireBasePicture;
    }

    public Bitmap getFireBasePicture() {
        return fireBasePicture;
    }

    public void setFireBasePicture(Bitmap fireBasePicture) {
        this.fireBasePicture = fireBasePicture;
    }
}
