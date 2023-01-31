package fr.ign.geosurvey.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import co.anbora.labs.spatia.geometry.Point;

@Entity
public class Marker {

    @PrimaryKey(autoGenerate = true)
    public int uid;
    public String name;
    public String address;
    public String comment;

    public Point position;

    public Marker() {
    }

    @Ignore
    public Marker(String name, String address, String comment, Point position) {
        this.name = name;
        this.address = address;
        this.comment = comment;
        this.position = position;
    }
}
