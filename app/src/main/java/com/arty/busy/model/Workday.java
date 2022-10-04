package com.arty.busy.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity
public class Workday {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "time_beginning")
    public long time_beginning;

    @ColumnInfo(name = "time_ending")
    public long time_ending;

    public Workday() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workday workday = (Workday) o;
        return uid == workday.uid && Objects.equals(time_beginning, workday.time_beginning) && Objects.equals(time_ending, workday.time_ending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, time_beginning, time_ending);
    }
}
