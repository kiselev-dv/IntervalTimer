package me.osm.dkiselev.intervaltimer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by dkiselev on 2/2/18.
 */

@Entity(tableName = "timers")
public class Timer {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "sets")
    private int sets;

    @ColumnInfo(name = "work")
    private int work;

    @ColumnInfo(name = "rest")
    private int rest;

    @ColumnInfo(name = "label")
    private String label;

    public Timer(int sets, int work, int rest, String label) {
        this.sets = sets;
        this.work = work;
        this.rest = rest;
        this.label = label;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
