/*******************************************************************************
 * Copyright (c) <2016> <Novemser>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 *  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

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
