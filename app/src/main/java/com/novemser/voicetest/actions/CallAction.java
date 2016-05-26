package com.novemser.voicetest.actions;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.regex.Pattern;

/**
 * Created by Novemser on 5/26/2016.
 */
public class CallAction extends BaseAction {
    public static Pattern pattern = Pattern.compile("(.*)(拨打|呼叫|call|打电话)(.*)");

    private static String number;

    public static boolean makeCallTo(String person) {
        if (person==null || person.equals(""))
            return false;
        number = null;

        if (isPhoneNumber(person))
            number = person;
        else
            number = getNumberByName(person);

        if (null != number) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    private static boolean isPhoneNumber(String number) {
        return number.matches("^\\d+\\D?$");
    }

    private static String getNumberByName(String name) {
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
