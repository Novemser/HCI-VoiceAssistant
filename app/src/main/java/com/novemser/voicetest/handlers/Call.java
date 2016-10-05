package com.novemser.voicetest.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.novemser.voicetest.R;
import com.novemser.voicetest.actions.CallAction;
import com.novemser.voicetest.utils.ChatMessage;

import java.util.HashMap;

/**
 * Created by Novemser on 2016/10/5.
 */
public class Call extends ActHandler {
    @Override
    public void doCMD(HashMap map, Handler mHandler, Context context) {
        if (map.containsKey("code"))
            CallAction.makeCallTo((String) map.get("code"));
        else if (map.containsKey("name"))
            CallAction.makeCallTo((String) map.get("name"));
            // 没有指定打给谁
        else {
            Message message = Message.obtain();
            message.obj = new ChatMessage(ChatMessage.Type.INPUT, context.getString(R.string.error_calling_content));
            mHandler.sendMessage(message);
            return;
        }
        // 打电话成功
        Message message = Message.obtain();
        message.obj = new ChatMessage(ChatMessage.Type.INPUT, context.getString(R.string.intent_recognized_text));
        mHandler.sendMessage(message);
    }
}
