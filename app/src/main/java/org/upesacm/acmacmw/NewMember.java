package org.upesacm.acmacmw;

public class NewMember {

    String fullName,branch,year,
            email;
    int sapId;
    int phoneNo;
    int whatsappNo;
    int rollNo;
    boolean premium;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSapId() {
        return sapId;
    }

    public void setSapId(int sapId) {
        this.sapId = sapId;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public int getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(int whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }


}
