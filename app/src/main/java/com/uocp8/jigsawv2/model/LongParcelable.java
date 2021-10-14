package com.uocp8.jigsawv2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LongParcelable  implements Parcelable {

    public static final Parcelable.Creator<LongParcelable> CREATOR = new
            Parcelable.Creator<LongParcelable>() {
                @Override
                public LongParcelable createFromParcel(Parcel in) {
                    return new LongParcelable(in);
                }

                @Override
                public LongParcelable[] newArray(int size) {
                    return new LongParcelable[size];
                }
            };
    private long data;

    public LongParcelable(long in) {
        data = in;
    }

    private LongParcelable(Parcel in) {
        data = in.readLong();
    }

    public long getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(data);
    }
}
