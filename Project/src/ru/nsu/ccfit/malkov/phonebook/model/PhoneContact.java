package ru.nsu.ccfit.malkov.phonebook.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @author malkov
 */
public class PhoneContact implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String name;

    @DatabaseField
    private String icon;

    @DatabaseField
    private String number;

    @DatabaseField
    private String email;

    public PhoneContact() {

    }

    public PhoneContact(String name, String icon, String number, String email) {
        this.name = name;
        this.icon = icon;
        this.number = number;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
