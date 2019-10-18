package helper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

import database.ContactNumberTable;
import database.ContactTable;
import database.EmailTable;
import listener.PublishProgressListener;
import jdo.Contact;
import jdo.ContactNumber;
import jdo.Email;

import static android.content.Context.MODE_PRIVATE;
import static constants.Constants.IS_DATA_FETCHED;
import static constants.Constants.PREFERENCE_NAME;

public class ContactsFromContentProvider {
    Context mContext;
    public PublishProgressListener mListener;
    ArrayList<Contact> mContactArrayList = new ArrayList<>();


    public ContactsFromContentProvider(Context pContext) {
        mContext = pContext;
        mListener = (PublishProgressListener) pContext;
    }

    public ArrayList<Contact> fetchContactsFromContentProvider() {

        int lCursor_position;
        ContentResolver lContentResolver = mContext.getContentResolver();
        Cursor lMobilecursor = null, lEmailcursor = null, lImagecursor = null;
        String lId_of_contact, lName_of_contact, lImg_url;
        HashSet<String> lNumberlist;
        ArrayList<String> lNumberTypeList;
        ArrayList<String> lEmaillist;
        ArrayList<String> lEmailTypeList;
        Uri lContactUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor lContactCursor = lContentResolver.query(lContactUri, null,
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE");
        if (lContactCursor != null && lContactCursor.moveToFirst()) {
            int lCursor_total_count = lContactCursor.getCount();
            while (lContactCursor.moveToNext()) {
                Contact lContact = new Contact();
                /*Contact Info*/
                lId_of_contact = lContactCursor.getString(lContactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                lName_of_contact = lContactCursor.getString(lContactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                lContact.setId(lId_of_contact);
                lContact.setName(lName_of_contact);
                lImagecursor = lContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + lId_of_contact,
                        null,
                        null);
                if (lImagecursor != null && lImagecursor.moveToFirst()) {

                    lImg_url = lImagecursor.getString(lImagecursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    lContact.setImg_url(lImg_url);
                }

                /*Mobile Number*/
                lMobilecursor = lContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.TYPE},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + lId_of_contact,
                        null,
                        null);
                Log.d("cursor", DatabaseUtils.dumpCursorToString(lMobilecursor));
                if (lMobilecursor != null && lMobilecursor.moveToFirst()) {
                    String phoneNumber_type;
                    lNumberlist = new HashSet<>();
                    lNumberTypeList = new ArrayList<>();
                    do {
                        String phoneNumber = lMobilecursor.getString(lMobilecursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int phoneNumber_type_number = lMobilecursor.getInt(lMobilecursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        if (phoneNumber_type_number == 1) {
                            phoneNumber_type = "Home";
                        } else if (phoneNumber_type_number == 2) {
                            phoneNumber_type = "Mobile";
                        } else if (phoneNumber_type_number == 3) {
                            phoneNumber_type = "Work";
                        } else {
                            phoneNumber_type = "Default";
                        }
                        lNumberlist.add(phoneNumber);
                        lNumberTypeList.add(phoneNumber_type);
                    } while (lMobilecursor.moveToNext());
                    ArrayList<String> lNumberlistFromSet = new ArrayList<>(lNumberlist);
                    lContact.setNumber(lNumberlistFromSet);
                    lContact.setNumber_type(lNumberTypeList);
                }

                /*Email Info*/
                lEmailcursor = lContentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + lId_of_contact,
                        null,
                        null);
                if (lEmailcursor != null && lEmailcursor.moveToFirst()) {
                    String email_type;
                    lEmaillist = new ArrayList<>();
                    lEmailTypeList = new ArrayList<>();
                    do {
                        String emailList = lEmailcursor.getString(lEmailcursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        int email_type_number = lEmailcursor.getInt(lEmailcursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        if (email_type_number == 1) {
                            email_type = "Home";
                        } else if (email_type_number == 2) {
                            email_type = "Mobile";
                        } else if (email_type_number == 3) {
                            email_type = "Work";
                        } else {
                            email_type = "Default";
                        }
                        lEmaillist.add(emailList);
                        lEmailTypeList.add(email_type);

                    } while (lEmailcursor.moveToNext());
                    lContact.setEmail(lEmaillist);
                    lContact.setEmail_type(lEmailTypeList);
                }

                mContactArrayList.add(lContact);
                lCursor_position = lContactCursor.getPosition();

                if (lCursor_position < lCursor_total_count) {
                    mListener.publishProgressUpdate(lCursor_position, (lCursor_position * 100 / lCursor_total_count), lCursor_total_count);
                }
            }

            ContactTable contactTableInstance = new ContactTable(mContext);
            contactTableInstance.insertContactDetails(mContactArrayList);

            ArrayList<ContactNumber> lContactNumberList = new ArrayList<>();
            ContactNumber lContactNumber;
            for (int i = 0; i < mContactArrayList.size(); i++) {
                lContactNumber = new ContactNumber();
                lContactNumber.setId(mContactArrayList.get(i).getId());
                lContactNumber.setNumber(mContactArrayList.get(i).getNumber());
                lContactNumber.setNumber_type(mContactArrayList.get(i).getNumber_type());
                lContactNumberList.add(lContactNumber);
            }
            ContactNumberTable contactNumberTableInstance = new ContactNumberTable(mContext);
            contactNumberTableInstance.insertContactNumberDetails(lContactNumberList);


            ArrayList<Email> lEmailList = new ArrayList<>();
            Email lEmail;
            for (int i = 0; i < mContactArrayList.size(); i++) {
                lEmail = new Email();
                lEmail.setId(mContactArrayList.get(i).getId());
                lEmail.setEmail(mContactArrayList.get(i).getEmail());
                lEmail.setEmail_type(mContactArrayList.get(i).getEmail_type());
                lEmailList.add(lEmail);
            }
            EmailTable emailTableInstance = new EmailTable(mContext);
            emailTableInstance.insertContactNumberDetails(lEmailList);


            SharedPreferences.Editor sharedpreference = mContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
            sharedpreference.putBoolean(IS_DATA_FETCHED, true);
            sharedpreference.apply();

            lContactCursor.close();
            lMobilecursor.close();
            lEmailcursor.close();
            lImagecursor.close();
        }

        return mContactArrayList;

    }


}

