package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewMember implements Parcelable{

    public static final Creator<NewMember> CREATOR = new Creator<NewMember>() {
        @Override
        public NewMember createFromParcel(Parcel in) {
            boolean array[]=new boolean[1];
            in.readBooleanArray(array);
            return new NewMember.Builder()
                    .setFullName(in.readString())
                    .setBranch(in.readString())
                    .setYear(in.readString())
                    .setEmail(in.readString())
                    .setSapId(in.readString())
                    .setPhoneNo(in.readString())
                    .setWhatsappNo(in.readString())
                    .setPremium(array[0])
                    .build();
        }

        @Override
        public NewMember[] newArray(int size) {
            return new NewMember[size];
        }
    };

    private String fullName, branch, year,
            email;
    private String sapId;
    private String phoneNo;
    private String whatsappNo;

    protected NewMember(Parcel in) {
        fullName = in.readString();
        branch = in.readString();
        year = in.readString();
        email = in.readString();
        sapId = in.readString();
        phoneNo = in.readString();
        whatsappNo = in.readString();
        otp = in.readString();
        premium = in.readByte() != 0;
    }



    private String otp;
    private boolean premium;

    public NewMember() {

    }

    public String getFullName() {
        return fullName;
    }

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

    public String getEmail() {
        return email;
    }

    public String getSapId() {
        return sapId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getWhatsappNo() {
        return whatsappNo;
    }

    public boolean isPremium() {
        return premium;
    }

    public String getOtp() {
        return otp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullName);
        parcel.writeString(branch);
        parcel.writeString(year);
        parcel.writeString(email);
        parcel.writeString(sapId);
        parcel.writeString(phoneNo);
        parcel.writeString(whatsappNo);
        parcel.writeString(otp);
        parcel.writeBooleanArray(new boolean[]{premium});

    }

    public static class Builder {

        String fullName;
        String branch;
        String year;
        String email;
        String sapId;
        String phoneNo;
        String whatsappNo;
        String otp;
        boolean premium;

        public NewMember build() {
            NewMember newMember=new NewMember();
            newMember.fullName=this.fullName;
            newMember.branch=this.branch;
            newMember.year=this.year;
            newMember.email=this.email;
            newMember.sapId=this.sapId;
            newMember.phoneNo=this.phoneNo;
            newMember.whatsappNo=this.whatsappNo;
            newMember.premium=this.premium;
            newMember.otp=this.otp;
            return newMember;
        }

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setBranch(String branch) {
            this.branch = branch;
            return this;
        }

        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setSapId(String sapId) {
            this.sapId = sapId;
            return this;
        }

        public Builder setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public Builder setWhatsappNo(String whatsappNo) {
            this.whatsappNo = whatsappNo;
            return this;
        }

        public Builder setPremium(boolean premium) {
            this.premium = premium;
            return this;
        }

        public Builder setOtp(String otp) {
            this.otp = otp;
            return this;
        }

    }

}
