package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    private String yearId;
    private String monthid;
    private ArrayList<String> likesIds;
    private String postId;

    public String getPostId() {
        return postId;
    }

    public String getYearId() {
        return yearId;
    }

    public String getMonthid() {
        return monthid;
    }

    public ArrayList<String> getLikesIds() {
        return likesIds;
    }

    public String getCaption() {
        return caption;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public Post() {}

    public Post(Parcel parcel) {
        imageUrl=parcel.readString();
        caption=parcel.readString();
        memberId=parcel.readString();
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
        String yearId;
        String monthId;
        String postId;
        ArrayList<String> likesIds;

        public Post build() {
            Post post=new Post();
            post.yearId=yearId;
            post.monthid=monthId;
            post.imageUrl=imageUrl;
            post.caption=caption;
            post.memberId=memberId;
            post.likesIds=likesIds;
            post.postId=postId;
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

        public Builder setYearId(String yearId) {
            this.yearId=yearId;
            return this;
        }

        public Builder setMonthId(String monthId) {
            this.monthId=monthId;
            return this;
        }

        public Builder setLikesCount(ArrayList<String> count) {
            this.likesIds=likesIds;
            return this;
        }

        public Builder setPostId(String postId) {
            this.postId=postId;
            return this;
        }
    }
}
