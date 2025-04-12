package com.arty.busy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.arty.busy.enums.Sex;

import java.util.Calendar;
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

    @ColumnInfo(name = "birth_date")
    public long birthDate;

    @ColumnInfo(name = "picture")
    public String picture;

    @ColumnInfo(name = "not_active")
    public boolean not_active;

    @ColumnInfo(name = "comment")
    public String comment;

    public Customer() {
        uid = 0;
        first_name = "";
        last_name = "";
        phone = "";
        sex = Sex.sex.name();
        birthDate = 0;
        picture = null;
        not_active = false;
        comment = "";
    }

    public Customer(Customer customer) {
        uid = customer.uid;
        first_name = customer.first_name;
        last_name = customer.last_name;
        phone = customer.phone;
        sex = customer.sex;
        birthDate = customer.birthDate;
        picture = customer.picture;
        not_active = customer.not_active;
        comment = customer.comment;
    }

    protected Customer(Parcel in) {
        uid = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        phone = in.readString();
        sex = in.readString();
        birthDate = in.readLong();
        picture = in.readString();
        not_active = in.readInt() != 0;
        comment = in.readString();
    }

    public static final Creator<Customer> CREATOR = new Creator<>() {
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
        return uid == that.uid && birthDate == that.birthDate && Objects.equals(first_name, that.first_name)
                && Objects.equals(last_name, that.last_name)
                && Objects.equals(phone, that.phone)
                && Objects.equals(sex, that.sex)
                && Objects.equals(picture, that.picture)
                && Objects.equals(comment, that.comment)
                && not_active == that.not_active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, first_name, last_name, phone, sex, birthDate, picture, not_active, comment);
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
        dest.writeLong(birthDate);
        dest.writeString(picture);
        dest.writeInt(not_active ? 1 : 0);
        dest.writeString(comment);
    }

    @NonNull
    @Override
    public String toString(){
        return last_name + " " + first_name;
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

    public int getAge(){
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTimeInMillis(this.birthDate);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // Проверка: уже был день рождения в этом году или нет
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
}
