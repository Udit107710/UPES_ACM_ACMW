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
    private String monthId;
    private String day;
    private String time;
    private ArrayList<String> likesIds;
    private String postId;

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }



    public String getPostId() {
        return postId;
    }

    public String getYearId() {
        return yearId;
    }

    public String getMonthId() {
        return monthId;
    }

    public ArrayList<String> getLikesIds() {
        if(likesIds==null)
            likesIds=new ArrayList<>();
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
        private String imageUrl;
        private String caption;
        private String memberId;
        private String yearId;
        private String monthId;
        private String day;
        private String time;
        private ArrayList<String> likesIds;
        private String postId;

        public Post build() {
            Post post=new Post();
            post.yearId=yearId;
            post.monthId=monthId;
            post.imageUrl=imageUrl;
            post.caption=caption;
            post.memberId=memberId;
            post.likesIds=likesIds==null?new ArrayList():likesIds;
            post.postId=postId;
            post.day=day;
            post.time=time;
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

        public Builder setLikesIds(ArrayList<String> likesIds) {
            this.likesIds=likesIds;
            return this;
        }

        public Builder setPostId(String postId) {
            this.postId=postId;
            return this;
        }

        public Builder setDay(String day) {
            this.day=day;
            return this;
        }

        public Builder setTime(String time) {
            this.time=time;
            return this;
        }
    }
}
