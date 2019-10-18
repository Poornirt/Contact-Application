package database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Contact_db";


    public SQLiteDatabase(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public android.database.sqlite.SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase pSqLiteDatabase) {
        ContactTable.createTable(pSqLiteDatabase);
        ContactNumberTable.createTable(pSqLiteDatabase);
        EmailTable.createTable(pSqLiteDatabase);
    }


    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
