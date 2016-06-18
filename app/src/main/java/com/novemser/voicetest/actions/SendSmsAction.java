package com.novemser.voicetest.actions;

import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;

import com.novemser.voicetest.ui.MainActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Novemser on 5/26/2016.
 */
public class SendSmsAction extends BaseAction {
    public static Pattern pattern = Pattern.compile("(.*)(发短信|发信息|发送信息|发送短信)(.*)");

    private static String number;

    public static boolean sendMessage(String person, String content, SmsManager manager) {
        if (person == null || person.equals("") || content == null || content.equals(""))
            return false;
        number = null;

        if (isPhoneNumber(person))
            number = person;
        else
            number = getNumberByName(person);

        ArrayList<String> list = manager.divideMessage(content);
        for (String text : list) {
            manager.sendTextMessage(number, null, text, null, null);
        }
        Snackbar.make(MainActivity.mMsg, "发送完毕~", Snackbar.LENGTH_LONG).show();
//        Toast.makeText(context, "发送完毕", Toast.LENGTH_SHORT).show();
        return true;
    }
}
