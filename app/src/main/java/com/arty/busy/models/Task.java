package com.arty.busy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(indices = {
        @Index(value = {"day", "date_time"})}
)
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "id_service")
    public int id_service;

    @ColumnInfo(name = "id_customer")
    public int id_customer;

    @ColumnInfo(name = "day")
    public long day;

    @ColumnInfo(name = "date_time")
    public long date_time;

    @ColumnInfo(name = "done")
    public boolean done;

    @ColumnInfo(name = "paid")
    public boolean paid;

    public Task() {
    }

    protected Task(Parcel in) {
        uid = in.readInt();
        id_service = in.readInt();
        id_customer = in.readInt();
        day = in.readLong();
        date_time = in.readLong();
        done = in.readByte() != 0;
        paid = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return uid == task.uid && id_service == task.id_service && id_customer == task.id_customer && day == task.day && date_time == task.date_time && done == task.done;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, id_service, id_customer, day, date_time, done);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(id_service);
        dest.writeInt(id_customer);
        dest.writeLong(day);
        dest.writeLong(date_time);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (paid ? 1 : 0));
    }
}
