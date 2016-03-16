package com.mis49m.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    //-- Database Version
    private static final int DATABASE_VERSION = 1;

    //-- Database Name
    private static final String DATABASE_NAME = "contactsDB";

    //-- Contacts table name
    private static final String TABLE_CONTACTS = "tblContacts";

    //-- Contacts Table Columns names
    private static final String COL_KEY_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phoneNumber";

    /* Constructor */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Create table on OnCreate()*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL Statement: CREATE TABLE tblContacts (id INTEGER PRIMARY KEY, name TEXT, phoneNumber TEXT))

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COL_KEY_ID + " INTEGER PRIMARY KEY," + COL_NAME + " TEXT,"
                + COL_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //-- drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        //-- create tables again
        onCreate(db);
    }

    /* SQLiteDatabase.delete : Convenience method for deleting rows in the database. */
    public int deleteContact(Contact contact) {
        // SQL Statement: DELETE FROM tblContacts WHERE id = ?
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_CONTACTS, 		// the table to delete from
                COL_KEY_ID + " = ?",	// the optional WHERE clause to apply when deleting. Passing null will delete all rows.
                new String[] { String.valueOf(contact.getId()) });	//  You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.

        //-- close resources
        db.close();

        return result;
    }

    /* SQLiteDatabase.update : Convenience method for updating rows in the database. */
    public int updateContact(Contact contact) {
        // SQL Statement: UPDATE tblContacts SET name=a, phoneNumber=1 WHERE id = ?
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to pass update method
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_PHONE, contact.getPhoneNumber());

        int result = db.update(
                TABLE_CONTACTS, 		// the table to update in
                values, 				// a map from column names to new column values. null is a valid value that will be translated to NULL.
                COL_KEY_ID + " = ?",	// the optional WHERE clause to apply when updating. Passing null will update all rows.
                new String[] { String.valueOf(contact.getId()) });  // You may include ?s in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings.

        //-- close resources
        db.close();

        return result;
    }

    public int getContactsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        //-- close resources
        cursor.close();
        db.close();

        return count;
    }

    /* SQLiteDatabase.insert : Convenience method for inserting a row into the database. */
    public long addContact(Contact contact) {
        // SQL Statement: INSERT tblContacts (name,phoneNumber) VALUES ('a','1')
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to pass insert method
        ContentValues values = new ContentValues();
        values.put(COL_NAME, contact.getName());
        values.put(COL_PHONE, contact.getPhoneNumber());

        //-- insert new contact row
        long result = db.insert(
                TABLE_CONTACTS, // the table to insert the row into
                null, 			// nullColumnHack, optional; may be null.
                values);		// this map contains the initial column values for the row. The keys should be the column names and the values the column values

        //-- close database connection
        db.close();

        return result;
    }

    /* SQLiteDatabase.query : Query the given table, returning a Cursor over the result set. */
    public Contact getContact(int id) {
        // SELECT id,name,phoneNumber FROM tblContacts
        SQLiteDatabase db = this.getReadableDatabase();
        Contact contact = new Contact();

        Cursor cursor = db.query(
                TABLE_CONTACTS, 	//  The table name to compile the query against.
                new String[] { COL_KEY_ID, COL_NAME, COL_PHONE },   //  A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
                COL_KEY_ID + "=?",	    // A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
                new String[] { String.valueOf(id) }, // You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
                null,       // A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
                null,       // A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
                null);      // How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.

        //-- Check cursor object for null
        if (cursor != null) {
            //-- Move the cursor to the first row
            cursor.moveToFirst();

            contact.setId(cursor.getInt(0));                //-- read id (0th col) from cursor
            contact.setName(cursor.getString(1));           //-- read name (1th col) from cursor
            contact.setPhoneNumber(cursor.getString(2));    //-- read phone (2th col) from cursor

        }

        //-- close cursor
        cursor.close();
        //-- close database connection
        db.close();

        return contact;
    }

    /* SQLiteDatabase.rawQuery : Runs the provided SQL and returns a Cursor over the result set. */
    public List<Contact> getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Contact> contactList = new ArrayList<Contact>();

        //-- select all query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        //-- run query on db
        Cursor cursor = db.rawQuery(
                selectQuery, // the SQL query. The SQL string must not be ; terminated
                null);		 // You may include ?s in where clause in the query, which will be replaced by the values from selectionArgs. The values will be bound as Strings.

        //-- loop through all rows and adding to list
        //-- Move the cursor to the first row
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));

                //-- add contact to list
                contactList.add(contact);
            } while (cursor.moveToNext()); // Move the cursor to the next row
        }

        //-- close resources
        cursor.close();
        db.close();

        //-- return contact list
        return contactList;
    }

}
