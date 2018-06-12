package org.upesacm.acmacmw.model;

public class Member {

    private String memberId;
    private String name;
    private String password;
    private String sap;
    private String branch;
    private String year;
    private String email;
    private String contact;

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
