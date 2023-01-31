package fr.ign.geosurvey.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import co.anbora.labs.spatia.geometry.GeometryConverters;

@Database(
        entities = {Marker.class, Topology.class},
        version = 1,
        exportSchema = false
)

@TypeConverters(GeometryConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MarkerDao markerDao();

    public abstract TopologyDao topologyDao();
}
