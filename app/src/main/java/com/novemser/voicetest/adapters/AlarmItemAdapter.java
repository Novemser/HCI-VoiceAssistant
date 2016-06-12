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

import com.novemser.voicetest.MainActivity;
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
//                        String longStr = cursor.getString(cursor.getColumnIndex("time"));
                        String contentStr = cursor.getString(cursor.getColumnIndex("content"));
//                        Log.d("DBRemove", "TargetId:" + id + " Position:" + position);
//                        Log.d("DBRemove", "TargetId:" + id + " " + content.getText() + " " + contentStr);
//                        Log.d("DBRemove", time.getText() + " " + longStr);
                        if (contentStr.equals(content.getText())) {
                            database.delete("alarm", "_id = ?", new String[]{String.valueOf(id)});
                            break;
                        }
                    }
                    notifyItemRemoved(position);
                    itemSettings.remove(position);
//                    YoYo.with(Techniques.FadeOut).duration(618).playOn(holder.itemView);
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
