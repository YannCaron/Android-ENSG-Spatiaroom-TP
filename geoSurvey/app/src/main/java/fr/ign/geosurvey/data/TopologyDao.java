package fr.ign.geosurvey.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TopologyDao {

    @Query("SELECT * FROM Topology")
    List<Topology> getAll();

    @Insert
    void insertAll(Topology... entries);

    @Update
    void update(Topology entry);

    @Delete
    void delete(Topology entry);

}
