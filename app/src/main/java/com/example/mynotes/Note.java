package com.example.mynotes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note  implements Parcelable {
    private int id;
    private String text;
    private Date date;

    public Note(int id, String text, Date date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate(){return date;}

    public void setDate(Date date){this.date = date;}

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(text);
        out.writeLong(date.getTime());
    }

    private Note(Parcel in) {
        id = in.readInt();
        text = in.readString();
        date = new Date(in.readLong());
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };


}
