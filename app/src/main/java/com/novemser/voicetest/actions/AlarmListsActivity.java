package com.novemser.voicetest.actions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.novemser.voicetest.ui.MainActivity;
import com.novemser.voicetest.R;
import com.novemser.voicetest.adapters.AlarmItemAdapter;
import com.novemser.voicetest.utils.AlarmItemSetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmListsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("闹钟");
        setSupportActionBar(toolbar);
        SQLiteDatabase database = MainActivity.db;
        List<AlarmItemSetting> itemSettings = new ArrayList<>();
        try {

            Cursor cursor = database.rawQuery("select * from alarm", null);
            while (cursor.moveToNext()) {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String longStr = cursor.getString(cursor.getColumnIndex("time"));
                AlarmItemSetting itemSetting = new AlarmItemSetting(content, new Date(Long.parseLong(longStr)));
                itemSettings.add(itemSetting);
                Log.d("DBInfo", " " + content + " " + longStr);
            }
        } catch (Exception e) {
            return;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvContacts);
        recyclerView.setAdapter(new AlarmItemAdapter(itemSettings));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
