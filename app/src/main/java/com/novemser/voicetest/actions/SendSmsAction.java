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
