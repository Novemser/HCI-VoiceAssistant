package com.novemser.voicetest.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Novemser on 5/30/2016.
 */
public class AlarmActivity extends AppCompatActivity {
    private String text;
    private long exact;
    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase database = openOrCreateDatabase("alarm.db", Context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from alarm", null);
        long currentTime = System.currentTimeMillis();
        long gap = currentTime;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String longStr = cursor.getString(cursor.getColumnIndex("time"));
            long time = Long.parseLong(longStr);
            if (Math.abs(time - currentTime) < gap) {
                gap = Math.abs(time - currentTime);
                text = cursor.getString(cursor.getColumnIndex("content"));
                exact = time;
                _id = id;
            }
        }
        
        database.delete("alarm", "_id = ?", new String[] {String.valueOf(_id)});

        new AlertDialog.Builder(AlarmActivity.this).setTitle("提醒")
                .setMessage(text)
                .setPositiveButton("知道啦", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmActivity.this.finish();
                    }
                }).show();


    }
}
