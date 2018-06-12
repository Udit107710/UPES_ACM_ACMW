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

    private String imageUrl;
    private String caption;
    private String memberId;

    public String getCaption() {
        return caption;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public Post() {}

    public Post(Parcel parcel) {
        imageUrl=parcel.readString();
        caption=parcel.readString();
        memberId=parcel.readString();
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
        parcel.writeString(caption);
        parcel.writeString(memberId);
    }

    public static class Builder {
        String imageUrl;
        String caption;
        String memberId;
        public Post build() {
            Post post=new Post();
            post.imageUrl=imageUrl;
            post.caption=caption;
            post.memberId=memberId;

            return post;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl=imageUrl;
            return this;
        }

        public Builder setCaption(String caption) {
            this.caption=caption;
            return this;
        }

        public Builder setMemberId(String memberId) {
            this.memberId=memberId;
            return this;
        }
    }
}
