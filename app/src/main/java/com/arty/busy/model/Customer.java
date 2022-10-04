package com.arty.busy.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Customer implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "first_name")
    public String first_name;

    @ColumnInfo(name = "last_name")
    public String last_name;

    @ColumnInfo(name = "number")
    public String number;

    @ColumnInfo(name = "sex")
    public String sex;

    @ColumnInfo(name = "age")
    public int age;

    public Customer() {
    }

    protected Customer(Parcel in) {
        uid = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        number = in.readString();
        sex = in.readString();
        age = in.readInt();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer that = (Customer) o;
        return uid == that.uid && age == that.age && Objects.equals(first_name, that.first_name) && Objects.equals(last_name, that.last_name) && Objects.equals(number, that.number) && Objects.equals(sex, that.sex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, first_name, last_name, number, sex, age);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(number);
        dest.writeString(sex);
        dest.writeInt(age);
    }
}
