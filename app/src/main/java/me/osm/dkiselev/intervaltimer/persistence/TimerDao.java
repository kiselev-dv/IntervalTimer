package me.osm.dkiselev.intervaltimer.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.osm.dkiselev.intervaltimer.model.Timer;

/**
 * Created by dkiselev on 2/2/18.
 */

@Dao
public interface TimerDao {

    @Query("SELECT * FROM timers")
    List<Timer> getAll();

    @Query("SELECT * FROM timers WHERE id IN (:ids)")
    List<Timer> loadAllByIds(int[] ids);

    @Insert
    long[] insertAll(Timer... timers);

    @Delete
    void delete(Timer timer);

    @Update
    void update(Timer timer);
}
