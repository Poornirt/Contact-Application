package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import jdo.Contact;

public class ContactTable {


    private Contact mContact;
    private ArrayList<Contact> mContactArrayList = new ArrayList<>();
    private EmailTable mEmailTable;
    private ContactNumberTable mContatcNumberTable;


    private static final String TABLE_NAME = "CONTACTS";
    private static final String COLUMN_ID = "CONTACT_ID";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_IMG_URL = "IMG_URL";
    private SQLiteDatabase mDBHelper;
    private Context mContext;

    private ContactTable() {

    }

    public ContactTable(Context pContext) {
        mContext = pContext;
        mDBHelper = new SQLiteDatabase(pContext);
    }

    public static void createTable(android.database.sqlite.SQLiteDatabase pSqLiteDatabase) {
        pSqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_NAME + " TEXT," +
                COLUMN_IMG_URL + " TEXT)");
    }

    public void insertContactDetails(ArrayList<Contact> pContactArrayList) {
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mDBHelper.getWritableDatabase();
        try {
            lSqLiteDatabase.beginTransaction();
            ContentValues lContentValues = new ContentValues();
            for (Contact contact : pContactArrayList) {
                lContentValues.put(COLUMN_ID, contact.getId());
                lContentValues.put(COLUMN_NAME, contact.getName());
                lContentValues.put(COLUMN_IMG_URL, contact.getImg_url());
                lSqLiteDatabase.insert(TABLE_NAME, null, lContentValues);
            }
            lSqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lSqLiteDatabase.endTransaction();
        }
    }

    public void deleteContact(Contact contact) {
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mDBHelper.getWritableDatabase();
        String contact_id = contact.getId();
        lSqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "= " + contact_id, null);
    }


    public ArrayList<Contact> fetchContactsFromDatabase() {
        Cursor lContactCursor;
        ArrayList<String> lEmail;
        ArrayList<String> lEmailType;
        ArrayList<String> lNumber;
        ArrayList<String> lNumberType;

        mEmailTable = new EmailTable(mContext);
        mContatcNumberTable = new ContactNumberTable(mContext);
        String lTable_Name_Contacts = "CONTACTS";
        String lfetchAllFromDatabase = "SELECT  * FROM " + lTable_Name_Contacts;
        android.database.sqlite.SQLiteDatabase lSqLiteDatabase = mDBHelper.getReadableDatabase();
        if (lSqLiteDatabase != null) {
            lContactCursor = lSqLiteDatabase.rawQuery(lfetchAllFromDatabase +" ORDER BY "+COLUMN_NAME + " COLLATE NOCASE ", null);
            if (lContactCursor.moveToFirst()) {
                do {
                    mContact = new Contact();
                    String lId = lContactCursor.getString(lContactCursor.getColumnIndex("CONTACT_ID"));
                    String lName = lContactCursor.getString(lContactCursor.getColumnIndex("NAME"));
                    String lImg_url = lContactCursor.getString(lContactCursor.getColumnIndex("IMG_URL"));
                    mContact.setId(lId);
                    mContact.setName(lName);
                    mContact.setImg_url(lImg_url);

                    lEmail=mEmailTable.fetchEmailFromDB(lId).getEmail();
                    mContact.setEmail(lEmail);

                    lEmailType=mEmailTable.fetchEmailFromDB(lId).getEmail_type();
                    mContact.setEmail_type(lEmailType);

                    lNumber=mContatcNumberTable.fetchContactNumberFromDB(lId).getNumber();
                    mContact.setNumber(lNumber);

                    lNumberType=mContatcNumberTable.fetchContactNumberFromDB(lId).getNumber_type();
                    mContact.setNumber_type(lNumberType);


                    mContactArrayList.add(mContact);
                } while (lContactCursor.moveToNext());
                lContactCursor.close();
            }


        }
        return mContactArrayList;
    }
}
