package org.upesacm.acmacmw;

public class AlumniDetail {

    private String Name;
    private String Position;
    private String Session;
    private String Image;


    public AlumniDetail(String name, String position, String session, String image) {
        Name = name;
        Position = position;
        Session = session;
        Image = image;
    }

    public AlumniDetail() {
    }

    public String getName() {
        return Name;
    }

    public String getPosition() {
        return Position;
    }

    public String getSession() {
        return Session;
    }

    public String getImage() {
        return Image;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public void setSession(String session) {
        Session = session;
    }

    public void setImage(String image) {
        Image = image;
    }
}
