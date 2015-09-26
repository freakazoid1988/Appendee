package com.cfg.appendee.objects;

import java.util.GregorianCalendar;

/**
 * Created by davem on 26/09/2015.
 */
public class Event {

    private String name, location, province;
    private GregorianCalendar date;
    private int ID;


    public Event(int ID, String name, String location, String province, GregorianCalendar date) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.province = province;
        this.date = date;
    }

    public Event() {

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public String toString() {
        return name + ", " + location;
    }
}
