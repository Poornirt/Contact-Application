package jdo;

import java.util.ArrayList;

public class Email {

    String mId;
    private ArrayList<String> mEmail = new ArrayList<>();
    private ArrayList<String> mEmail_type = new ArrayList<>();

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
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
        return "Email{" +
                "mId='" + mId + '\'' +
                ", mEmail=" + mEmail +
                ", mEmail_type=" + mEmail_type +
                '}';
    }
}
