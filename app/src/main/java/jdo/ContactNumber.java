package jdo;

import java.util.ArrayList;

public class ContactNumber {

    private String mId;
    private ArrayList<String> mNumber=new ArrayList<>();
    private ArrayList<String> mNumber_type=new ArrayList<>();

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
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

    @Override
    public String toString() {
        return "ContactNumber{" +
                "mId='" + mId + '\'' +
                ", mNumber=" + mNumber +
                ", mNumber_type=" + mNumber_type +
                '}';
    }
}
