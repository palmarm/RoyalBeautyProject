package com.example.royalbeautyproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {
    private String name;
    private double price;
    private String category;
    private boolean isSelected;
    private int imageResId;

    public Service(String name, double price, String category, int imageResId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageResId = imageResId;
        this.isSelected = false;
    }

    protected Service(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        category = in.readString();
        isSelected = in.readByte() != 0;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getImageResId() {
        return imageResId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(category);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
