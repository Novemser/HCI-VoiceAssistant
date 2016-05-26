package com.novemser.voicetest.actions;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Novemser on 5/26/2016.
 */
public abstract class BaseAction {
    public static Context context;

    public static boolean isPhoneNumber(String number) {
        return number.matches("^\\d+\\D?$");
    }

    public static String getNumberByName(String name) {
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[] { ContactsContract.Contacts._ID }, null, null, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            long id = cursor.getLong(idCoulmn);
            cursor.close();
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] { "data1" }, "contact_id = ?", new String[] { Long.toString(id) }, null);
            if ((cursor != null) && (cursor.moveToFirst())) {
                int m = cursor.getColumnIndex("data1");
                String num = cursor.getString(m);
                cursor.close();
                return num;
            }
        }
        return null;
    }
}
