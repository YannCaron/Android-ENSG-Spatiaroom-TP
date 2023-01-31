package fr.ign.geosurvey.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MarkerDao {

    @Query("SELECT * FROM Marker")
    List<Marker> getAll();

    @Insert
    void insertAll(Marker... entries);

    @Update
    void update(Marker entry);

    @Delete
    void delete(Marker entry);

}
