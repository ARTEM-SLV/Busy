package com.arty.busy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.arty.busy.enums.Sex;

import java.util.Objects;

@Entity
public class Customer implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "first_name")
    public String first_name;

    @ColumnInfo(name = "last_name")
    public String last_name;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "sex")
    public String sex;

    @ColumnInfo(name = "age")
    public int age;

    @ColumnInfo(name = "picture")
    public String picture;

    public Customer() {
        uid = 0;
        first_name = "";
        last_name = "";
        phone = "";
        sex = Sex.sex.name();
        age = 0;
        picture = null;
    }

    public Customer(Customer customer) {
        uid = customer.uid;
        first_name = customer.first_name;
        last_name = customer.last_name;
        phone = customer.phone;
        sex = customer.sex;
        age = customer.age;
        picture = customer.picture;
    }

    protected Customer(Parcel in) {
        uid = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        phone = in.readString();
        sex = in.readString();
        age = in.readInt();
        picture = in.readString();
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
        return uid == that.uid && age == that.age && Objects.equals(first_name, that.first_name)
                && Objects.equals(last_name, that.last_name)
                && Objects.equals(phone, that.phone)
                && Objects.equals(sex, that.sex)
                && Objects.equals(picture, that.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, first_name, last_name, phone, sex, age, picture);
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
        dest.writeString(phone);
        dest.writeString(sex);
        dest.writeInt(age);
        dest.writeString(picture);
    }

    @NonNull
    @Override
    public String toString(){
        return first_name + " " + last_name;
    }

    public String shortTitle(){
        StringBuffer result = new StringBuffer();
        String[] arrFirstName;
        String[] arrLastName;

        if (first_name == null || first_name.isEmpty()){
            return "";
        } else {
            arrFirstName = first_name.split(" ");
        }
        supplementResult(arrFirstName, result);

        if (last_name == null || last_name.isEmpty()){
            return String.valueOf(first_name.charAt(0));
        } else {
            arrLastName = last_name.split(" ");
        }
        supplementResult(arrLastName, result);

        return result.toString();
    }

    private void supplementResult(@NonNull String[] arrFirstName, StringBuffer result){
        for (String str: arrFirstName) {
            result.append(str.charAt(0));
        }
    }
}
