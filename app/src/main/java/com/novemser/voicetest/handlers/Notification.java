package com.novemser.voicetest.handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.novemser.voicetest.R;
import com.novemser.voicetest.ui.AlarmActivity;
import com.novemser.voicetest.ui.MainActivity;
import com.novemser.voicetest.utils.ChatMessage;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Novemser on 2016/10/5.
 */
public class Notification extends ActHandler {

    private void insertDate(SQLiteDatabase database, String content, Long time) {
        database.execSQL("insert into alarm values(null, ?, ?)", new String[] {content, String.valueOf(time)});
    }

    public void doCMD(HashMap map, Handler mHandler, Context context, SQLiteDatabase db) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (map.containsKey("date") && map.containsKey("time")) {
            try {
                // 用户设置了具体的时间
                String dateRaw = (String) map.get("date");
                String[] dateStr = dateRaw.split("-");
                int[] date = new int[3];
                for (int i = 0; i < 3; i++)
                    date[i] = Integer.parseInt(dateStr[i]);
                // 注意，date的月份是0-11
                calendar.set(date[0], date[1] - 1, date[2]);

                String timeRaw = (String) map.get("time");
                String[] timeStr = timeRaw.split(":");
                int[] time = new int[3];
                for (int i = 0; i < 3; i++)
                    time[i] = Integer.parseInt(timeStr[i]);
                calendar.set(Calendar.HOUR_OF_DAY, time[0]);
                calendar.set(Calendar.MINUTE, time[1]);
                calendar.set(Calendar.SECOND, time[2]);
                Log.d("TimeSetFr", String.valueOf(System.currentTimeMillis()));
                Log.d("TimeSetTo", String.valueOf(calendar.getTimeInMillis()));
                // 设置闹钟
                Intent intent = new Intent(context, AlarmActivity.class);
                Log.d("FuckIntent", String.valueOf((calendar.getTimeInMillis() % 500)));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (calendar.getTimeInMillis() % 500), intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                try {
                    insertDate(db, (String) map.get("content"), calendar.getTimeInMillis());
                } catch (SQLiteException e) {
                    db.execSQL("create table alarm(_id integer primary key autoincrement," +
                            " content varchar(255)," +
                            " time BIGINT)");
                    insertDate(db, (String) map.get("content"), calendar.getTimeInMillis());
                }
                // 设置成功
                Message message = Message.obtain();
                message.obj = new ChatMessage(ChatMessage.Type.INPUT, context.getString(R.string.intent_recognized_text));
                mHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
