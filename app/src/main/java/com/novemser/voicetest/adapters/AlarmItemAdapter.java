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

package com.novemser.voicetest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.novemser.voicetest.ui.MainActivity;
import com.novemser.voicetest.R;
import com.novemser.voicetest.utils.AlarmItemSetting;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Novemser on 6/11/2016.
 */
public class AlarmItemAdapter extends RecyclerView.Adapter<AlarmItemAdapter.ViewHolder> {
    private List<AlarmItemSetting> itemSettings;
    SQLiteDatabase database;
    Cursor cursor;

    public AlarmItemAdapter(List<AlarmItemSetting> itemSettings) {
        this.itemSettings = itemSettings;
    }

    @Override
    public AlarmItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        database = MainActivity.db;
        cursor = database.rawQuery("select * from alarm", null);

        LayoutInflater inflater = LayoutInflater.from(context);

        View root = inflater.inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TextView content;
        final TextView time;
        AlarmItemSetting itemSetting = itemSettings.get(position);

        content = holder.content;
        content.setText(itemSetting.getContent());
        time = holder.time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        time.setText(simpleDateFormat.format(itemSetting.getTime()));
        holder.switchCompat.setChecked(true);
        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex("_id"));
                        String contentStr = cursor.getString(cursor.getColumnIndex("content"));
                        if (contentStr.equals(content.getText())) {
                            database.delete("alarm", "_id = ?", new String[]{String.valueOf(id)});
                            break;
                        }
                    }
                    try {
                        itemSettings.remove(position);
                        notifyItemRemoved(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemSettings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView time;
        public SwitchCompat switchCompat;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            content = (TextView) itemView.findViewById(R.id.alarm_content);
            time = (TextView) itemView.findViewById(R.id.alarm_time);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.alarm_switch);
        }
    }
}
