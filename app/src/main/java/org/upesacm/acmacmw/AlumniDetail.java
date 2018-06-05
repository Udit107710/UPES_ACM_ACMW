package org.upesacm.acmacmw;

public class AlumniDetail {

    private int image;
    private String name, designation, session;

    public AlumniDetail(int image, String name, String designation, String session) {
        this.image = image;
        this.name = name;
        this.designation = designation;
        this.session = session;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getSession() {
        return session;
    }
}
