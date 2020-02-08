package com.vaibhav.android.checklist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Checklist implements Parcelable {

    private int mID;
    private String mTitle;

    public Checklist(int ID, String title) {
        mID = ID;
        mTitle = title;
    }

    public int getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(mID);
        out.writeString(mTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Checklist> CREATOR = new Creator<Checklist>() {
        @Override
        public Checklist createFromParcel(Parcel in) {
            return new Checklist(in);
        }

        @Override
        public Checklist[] newArray(int size) {
            return new Checklist[size];
        }
    };

    private Checklist(Parcel in) {
        mID = in.readInt();
        mTitle = in.readString();
    }
}
