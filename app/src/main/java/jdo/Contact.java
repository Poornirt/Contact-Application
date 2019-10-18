package jdo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Contact implements Serializable {
    private String mId, mName, mImg_url;
    private ArrayList<String> mNumber =new ArrayList<>();
    private ArrayList<String> mNumber_type =new ArrayList<>();
    private ArrayList<String> mEmail =new ArrayList<>();
    private ArrayList<String> mEmail_type =new ArrayList<>();

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImg_url() {
        return mImg_url;
    }

    public void setImg_url(String mImg_url) {
        this.mImg_url = mImg_url;
    }

    public ArrayList<String> getNumber() {
        return mNumber;
    }

    public void setNumber(ArrayList<String> mNumber) {
        this.mNumber = mNumber;
    }

    public ArrayList<String> getNumber_type() {
        return mNumber_type;
    }

    public void setNumber_type(ArrayList<String> mNumber_type) {
        this.mNumber_type = mNumber_type;
    }

    public ArrayList<String> getEmail() {
        return mEmail;
    }

    public void setEmail(ArrayList<String> mEmail) {
        this.mEmail = mEmail;
    }

    public ArrayList<String> getEmail_type() {
        return mEmail_type;
    }

    public void setEmail_type(ArrayList<String> mEmail_type) {
        this.mEmail_type = mEmail_type;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mImg_url='" + mImg_url + '\'' +
                ", mNumber=" + mNumber +
                ", mNumber_type=" + mNumber_type +
                ", mEmail=" + mEmail +
                ", mEmail_type=" + mEmail_type +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(mId, contact.mId) &&
                Objects.equals(mName, contact.mName) &&
                Objects.equals(mImg_url, contact.mImg_url) &&
                Objects.equals(mNumber, contact.mNumber) &&
                Objects.equals(mNumber_type, contact.mNumber_type) &&
                Objects.equals(mEmail, contact.mEmail) &&
                Objects.equals(mEmail_type, contact.mEmail_type);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(mId, mName, mImg_url, mNumber, mNumber_type, mEmail, mEmail_type);
    }
}

