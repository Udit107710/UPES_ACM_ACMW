package org.upesacm.acmacmw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable{

    private String memberId;
    private String name;
    private String password;
    private String sap;
    private String branch;
    private String year;
    private String email;
    private String contact;

    Member() {}

    protected Member(Parcel in) {
        memberId = in.readString();
        name = in.readString();
        password = in.readString();
        sap = in.readString();
        branch = in.readString();
        year = in.readString();
        email = in.readString();
        contact = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getBranch() {
        return branch;
    }

    public String getYear() {
        return year;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getSap() {
        return sap;
    }
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(memberId);
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(sap);
        parcel.writeString(branch);
        parcel.writeString(year);
        parcel.writeString(email);
        parcel.writeString(contact);
    }

    public static class Builder {
        private String memberId;
        private String name;
        private String password;
        private String sap;
        private String branch;
        private String year;
        private String email;
        private String contact;

        public Member build() {
            Member member=new Member();
            member.memberId=memberId;
            member.name=name;
            member.password=password;
            member.sap=sap;
            member.branch=branch;
            member.year=year;
            member.email=email;
            member.contact=contact;
            return member;
        }

        public Builder setmemberId(String memberId) {
            this.memberId=memberId;
            return this;
        }

        public Builder setName(String name) {
            this.name=name;
            return this;
        }

        public Builder setPassword(String password) {
            this.password=password;
            return this;
        }

        public Builder setSAPId(String sap) {
            this.sap=sap;
            return this;
        }

        public Builder setEmail(String email) {
            this.email=email;
            return this;
        }

        public Builder setBranch(String branch) {
            this.branch=branch;
            return this;
        }

        public Builder setYear(String year) {
            this.year=year;
            return this;
        }

        public Builder setContact(String contact) {
            this.contact=contact;
            return this;
        }
    }
}
