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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
        TextView textView = (TextView) findViewById(R.id.tv_has_alarm);

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
        if (itemSettings.size() > 0)
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvContacts);
        recyclerView.setAdapter(new AlarmItemAdapter(itemSettings));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
