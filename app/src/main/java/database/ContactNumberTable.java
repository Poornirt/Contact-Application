package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import jdo.ContactNumber;

public class ContactNumberTable {

    private ArrayList<String> mNumber;
    private ArrayList<String> mNumber_type;

    private static final String TABLE_NAME = "MOBILE";
    private static final String COLUMN_NUMBER = "MOBILE_NUMBER";
    private static final String COLUMN_NUMBER_TYPE = "MOBILE_NUMBER_TYPE";
    private static final String COLUMN_CONTACT_ID = "CONTACT_ID";
    private SQLiteDatabase mDBHelper;

    public ContactNumberTable(Context pContext) {
        mDBHelper = new SQLiteDatabase(pContext);
    }

    public static void createTable(android.database.sqlite.SQLiteDatabase pSqLiteDatabase) {
        pSqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_CONTACT_ID + " INTEGER," +
                COLUMN_NUMBER + " TEXT," +
                COLUMN_NUMBER_TYPE + " TEXT)");
    }

    public void insertContactNumberDetails(ArrayList<ContactNumber> pContactNumberArrayList) {
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mDBHelper.getWritableDatabase();
        try {
            lSqLiteDatabase.beginTransaction();
            ContentValues lContentValues = new ContentValues();
            for (ContactNumber contact : pContactNumberArrayList) {
                lContentValues.put(COLUMN_CONTACT_ID, contact.getId());
                for (int i = 0; i < contact.getNumber().size(); i++) {
                    lContentValues.put(COLUMN_NUMBER, contact.getNumber().get(i));
                    lContentValues.put(COLUMN_NUMBER_TYPE, contact.getNumber_type().get(i));
                    lSqLiteDatabase.insert(TABLE_NAME, null, lContentValues);
                }
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lSqLiteDatabase.endTransaction();
            lSqLiteDatabase.close();
        }
    }

    public ContactNumber fetchContactNumberFromDB(String pId) {
        ContactNumber lContact_Number_Obj = new ContactNumber();
        Cursor lContactNumberCursor;
        String lTable_Name_Number = "MOBILE";
        String lfetchContactNumberFromDatabase = "SELECT * FROM " + lTable_Name_Number + " WHERE CONTACT_ID = " + pId;
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mDBHelper.getReadableDatabase();
        if (lSqLiteDatabase != null) {
            lContactNumberCursor = lSqLiteDatabase.rawQuery(lfetchContactNumberFromDatabase, null);
            if (lContactNumberCursor != null && lContactNumberCursor.moveToFirst()) {
                mNumber = new ArrayList<>();
                mNumber_type = new ArrayList<>();
                do {
                    String lNumber = lContactNumberCursor.getString(lContactNumberCursor.getColumnIndex("MOBILE_NUMBER"));
                    String lNumber_type = lContactNumberCursor.getString(lContactNumberCursor.getColumnIndex("MOBILE_NUMBER_TYPE"));
                    mNumber.add(lNumber);
                    mNumber_type.add(lNumber_type);
                } while (lContactNumberCursor.moveToNext());
                lContact_Number_Obj.setNumber(mNumber);
                lContact_Number_Obj.setNumber_type(mNumber_type);
                lContact_Number_Obj.setId(pId);
            }
            lContactNumberCursor.close();
        }
        return lContact_Number_Obj;
    }

}
