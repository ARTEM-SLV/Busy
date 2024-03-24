package com.arty.busy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(indices = {
        @Index(value = {"day"})}
)
public class Task implements Parcelable  {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "id_service")
    public int id_service;

    @ColumnInfo(name = "id_customer")
    public int id_customer;

    @ColumnInfo(name = "day")
    public long day;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "price")
    public double price;

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
        time = in.readString();
        duration = in.readInt();
        price = in.readDouble();
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
        Task that = (Task) o;
        return uid == that.uid && id_service == that.id_service && id_customer == that.id_customer && day == that.day && Double.compare(that.price, price)
                == 0 && done == that.done && paid == that.paid && time.equals(that.time) && duration == that.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, id_service, id_customer, day, time, duration, price, done, paid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(id_service);
        dest.writeInt(id_customer);
        dest.writeLong(day);
        dest.writeString(time);
        dest.writeInt(duration);
        dest.writeDouble(price);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (paid ? 1 : 0));
    }
}
