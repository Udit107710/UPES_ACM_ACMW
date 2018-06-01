package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable{
    public static final Parcelable.Creator CREATOR=new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Question(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new Question[i];
        }
    };

    String question;
    public Question() {}

    public Question(Parcel parcel) {
        question=parcel.readString();
    }

    public Question(String question) {
        this.question=question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question=question;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(question);
    }
}
