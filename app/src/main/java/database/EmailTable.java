package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import jdo.Email;

public class EmailTable {

    private ArrayList<String> mEmail;
    private ArrayList<String> mEmail_type;
    private static final String TABLE_NAME = "EMAIL";
    private static final String COLUMN_CONTACT_ID = "CONTACT_ID";
    private static final String COLUMN_EMAIL = "EMAIL";
    private static final String COLUMN_EMAIL_TYPE = "EMAIL_TYPE";
    private SQLiteDatabase mSqLiteDatabase;

    public EmailTable(Context pContext) {
        mSqLiteDatabase = new SQLiteDatabase(pContext);
    }

    public static void createTable(android.database.sqlite.SQLiteDatabase pSqLiteDatabase) {
        pSqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_CONTACT_ID + " INTEGER," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_EMAIL_TYPE + " TEXT)");
    }

    public void insertContactNumberDetails(ArrayList<Email> lEmailList) {
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mSqLiteDatabase.getWritableDatabase();
        try {
            lSqLiteDatabase.beginTransaction();
            ContentValues lContentValues = new ContentValues();
            for (Email email : lEmailList) {
                lContentValues.put(COLUMN_CONTACT_ID, email.getId());
                for (int i = 0; i < email.getEmail().size(); i++){
                    lContentValues.put(COLUMN_EMAIL, email.getEmail().get(i));
                    lContentValues.put(COLUMN_EMAIL_TYPE, email.getEmail_type().get(i));
                    lSqLiteDatabase.insert(TABLE_NAME, null, lContentValues);
                }
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }

    public Email fetchEmailFromDB(String pId){
        Email lEmail_Obj=new Email();
        Cursor lEmailCursor;
        String lTable_Name_Email = "EMAIL";
        String lFetchEmailFromDatabase = "SELECT * FROM " + lTable_Name_Email + " WHERE CONTACT_ID = " + pId;
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mSqLiteDatabase.getReadableDatabase();
        if(lSqLiteDatabase!=null){
            lEmailCursor = lSqLiteDatabase.rawQuery(lFetchEmailFromDatabase, null);
            if (lEmailCursor != null && lEmailCursor.moveToFirst()) {
                mEmail = new ArrayList<>();
                mEmail_type = new ArrayList<>();
                do {
                    String lEmail = lEmailCursor.getString(lEmailCursor.getColumnIndex("EMAIL"));
                    String lEmail_type = lEmailCursor.getString(lEmailCursor.getColumnIndex("EMAIL_TYPE"));
                    mEmail.add(lEmail);
                    mEmail_type.add(lEmail_type);
                } while (lEmailCursor.moveToNext());
               lEmail_Obj.setEmail(mEmail);
               lEmail_Obj.setEmail_type(mEmail_type);
               lEmail_Obj.setId(pId);
            }

            lEmailCursor.close();
        }
        return lEmail_Obj;
    }

}
