package com.arty.busy.models;

import android.os.Build;
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

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "id_line_of_business")
    public int id_line_of_business;

    @ColumnInfo(name = "not_active")
    public boolean not_active;

    public Service() {
        uid = 0;
        title = "";
        short_title = "";
        description = "";
        price = 0;
        duration = 0;
        id_line_of_business = 0;
        not_active = false;
    }

    public Service(Service service) {
        uid = service.uid;
        title = service.title;
        short_title = service.short_title;
        description = service.description;
        price = service.price;
        duration = service.duration;
        id_line_of_business = service.id_line_of_business;
        not_active = service.not_active;
    }

    protected Service(Parcel in) {
        uid = in.readInt();
        title = in.readString();
        short_title = in.readString();
        description = in.readString();
        price = in.readDouble();
        duration = in.readInt();
        id_line_of_business = in.readInt();
        not_active = in.readInt() != 0;
    }

    public static final Creator<Service> CREATOR = new Creator<>() {
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
        return uid == that.uid && Double.compare(that.price, price) == 0 && duration == that.duration
                && id_line_of_business == that.id_line_of_business && Objects.equals(title, that.title)
                && Objects.equals(short_title, that.short_title) && Objects.equals(description, that.description)
                && not_active == that.not_active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, title, short_title, description, price, duration, id_line_of_business, not_active);
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
        dest.writeInt(duration);
        dest.writeInt(id_line_of_business);
        dest.writeInt(not_active ? 1 : 0);
    }

    public String getShortTitle(){
        StringBuilder result  = new StringBuilder();

        // Разбиваем строку на слова по пробелам
        String[] words = title.split("\\s+");

        for (String word : words) {
            if (word.isEmpty()) continue;

            char firstChar = word.charAt(0);

            // Если слово начинается с цифры — добавляем все цифры
            if (Character.isDigit(word.charAt(0))) {
                for (char c : word.toCharArray()) {
                    result.append(Character.toUpperCase(c));
                    if (!Character.isDigit(c)) {
                        break;
                    }
                }
            } else {
                // Иначе добавляем первую букву
                result.append(Character.toUpperCase(firstChar));
            }
        }

        return result .toString();
    }
}
