package com.java.kaboome.presentation.entities;

import java.io.Serializable;

public class ContactModel implements Serializable {

    private String name;
    private String phone;
    private String email; //added for message attachment contact
    private String photoURI;
    private String lookupKey; //to be used as an id for contacts

    public ContactModel() {
    }

    public ContactModel(String name, String phone, String photoURI) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactModel contact = (ContactModel) o;
        return this.lookupKey.equals(contact.lookupKey);

    }

    @Override
    public int hashCode() {

        return lookupKey.hashCode();
    }

    @Override
    public String toString() {
        return "ContactModel{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", photoURI='" + photoURI + '\'' +
                ", lookupKey='" + lookupKey + '\'' +
                '}';
    }
}
