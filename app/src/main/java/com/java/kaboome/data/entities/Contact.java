package com.java.kaboome.data.entities;

public class Contact {

    private String name;
    private String phone;
    private String photoURI;
    private String lookupKey; //to be used as an id for contacts

    public Contact() {
    }

    public Contact(String name, String phone, String photoURI) {
        this.name = name;
        this.phone = phone;
        this.photoURI = photoURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", photoURI='" + photoURI + '\'' +
                ", lookupKey='" + lookupKey + '\'' +
                '}';
    }
}