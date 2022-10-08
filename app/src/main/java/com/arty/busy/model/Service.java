package com.arty.busy.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Service implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "short_title")
    public String short_title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "execution_time")
    public int execution_time;

    @ColumnInfo(name = "preparation_time")
    public int preparation_time;

    @ColumnInfo(name = "id_line_of_business")
    public int id_line_of_business;

    public Service() {
    }

    protected Service(Parcel in) {
        uid = in.readInt();
        title = in.readString();
        short_title = in.readString();
        description = in.readString();
        price = in.readDouble();
        execution_time = in.readInt();
        preparation_time = in.readInt();
        id_line_of_business = in.readInt();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service) o;
        return uid == that.uid && Double.compare(that.price, price) == 0 && execution_time == that.execution_time && preparation_time == that.preparation_time && id_line_of_business == that.id_line_of_business && Objects.equals(title, that.title) && Objects.equals(short_title, that.short_title) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, title, short_title, description, price, execution_time, preparation_time, id_line_of_business);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(title);
        dest.writeString(short_title);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(execution_time);
        dest.writeInt(preparation_time);
        dest.writeInt(id_line_of_business);
    }
}
