package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }
        @Override
        public Object[] newArray(int i) {
            return new Post[i];
        }
    };
    String imageUrl;

    public Post() {}

    public Post(Parcel parcel) {
        imageUrl=parcel.readString();
    }

    public Post(String imageUrl) {
        this.imageUrl=imageUrl;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl=imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageUrl);
    }
}
