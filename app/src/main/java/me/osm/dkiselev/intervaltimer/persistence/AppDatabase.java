package me.osm.dkiselev.intervaltimer.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import me.osm.dkiselev.intervaltimer.model.Timer;

/**
 * Created by dkiselev on 2/2/18.
 */
@Database(entities = {Timer.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerDao timerDao();
}
