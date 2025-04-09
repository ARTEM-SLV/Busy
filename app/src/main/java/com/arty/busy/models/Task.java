package com.arty.busy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity(indices = {
        @Index(value = {"day"})}
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

    @ColumnInfo(name = "comment")
    public String comment;

    public Task() {
        uid = 0;
        id_service = 0;
        id_customer = 0;
        day = 0;
        time = "";
        duration = 0;
        price = 0;
        done = false;
        paid = false;
        comment = "";
    }

    public Task(Task task) {
        uid = task.uid;
        id_service = task.id_service;
        id_customer = task.id_customer;
        day = task.day;
        time = task.time;
        duration = task.duration;
        price = task.price;
        done = task.done;
        paid = task.paid;
        comment = task.comment;
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
        comment = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<>() {
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
        return uid == that.uid && id_service == that.id_service && id_customer == that.id_customer
                && day == that.day && Double.compare(that.price, price) == 0
                && done == that.done && paid == that.paid && time.equals(that.time)
                && duration == that.duration && comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, id_service, id_customer, day, time, duration, price, done, paid, comment);
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
        dest.writeString(comment);
    }

    @NonNull
    @Override
    public String toString(){
        return "|uid: " + uid + "|id_service: " + id_service + "|id_customer: " + id_customer
                + "|day: " + new Date(day) + "|time: " + time + "|duration: " + duration + "|price: " + price;
    }
}
