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
    public int time_beginning;

    @ColumnInfo(name = "time_ending")
    public int time_ending;

    @ColumnInfo(name = "time_break_beginning")
    public int time_break_beginning;

    @ColumnInfo(name = "time_break_ending")
    public int time_break_ending;

    public Workday() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workday workday = (Workday) o;
        return uid == workday.uid && time_beginning == workday.time_beginning && time_ending == workday.time_ending && time_break_beginning == workday.time_break_beginning && time_break_ending == workday.time_break_ending;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, time_beginning, time_ending, time_break_beginning, time_break_ending);
    }
}
