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

        try {
            if (null != number) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
