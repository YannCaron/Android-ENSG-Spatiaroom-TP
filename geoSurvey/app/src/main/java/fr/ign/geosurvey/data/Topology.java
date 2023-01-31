package fr.ign.geosurvey.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import co.anbora.labs.spatia.geometry.LineString;

@Entity
public class Topology {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String name;
    public String comment;

    public LineString path;

    public Topology() {
    }

    @Ignore
    public Topology(String name, String comment, LineString path) {
        this.uid = uid;
        this.name = name;
        this.comment = comment;
        this.path = path;
    }
}
