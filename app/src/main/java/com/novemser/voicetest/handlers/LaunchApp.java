package com.novemser.voicetest.handlers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;

import com.novemser.voicetest.R;
import com.novemser.voicetest.actions.SendSmsAction;
import com.novemser.voicetest.utils.ChatMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Novemser on 2016/10/5.
 */
public class LaunchApp extends ActHandler {

    public void doCMD(HashMap map, Context context, PackageManager packageManager, List<ResolveInfo> resolveInfoList) {
        if (map.containsKey("name")) {
            String name = (String) map.get("name");

            if (name.contains("相机")) {
                Log.d("picture", "照相");
                Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA"); //调用照相机
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
            if (name.contains("录音机")) {
                Intent mi = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                mi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mi);
                return;
            }
            if (name.contains("联系人") || name.contains("通讯录")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Contacts.People.CONTENT_URI);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
            if (name.contains("地图")) {
                //显示地图:
                Uri uri = Uri.parse("geo:");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                return;
            }
            for (ResolveInfo res : resolveInfoList) {
                String pkg = res.activityInfo.packageName;
                String cls = res.activityInfo.name;
                String appName = res.loadLabel(packageManager).toString();
                if (appName.contains(name)) {
                    ComponentName component = new ComponentName(pkg, cls);
                    Intent i = new Intent();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(component);
                    context.startActivity(i);
                }
            }
        }
    }
}
