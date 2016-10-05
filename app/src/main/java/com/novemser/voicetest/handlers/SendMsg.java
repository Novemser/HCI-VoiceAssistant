package com.novemser.voicetest.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;

import com.novemser.voicetest.R;
import com.novemser.voicetest.actions.SendSmsAction;
import com.novemser.voicetest.utils.ChatMessage;

import java.util.HashMap;

/**
 * Created by Novemser on 2016/10/5.
 */
public class SendMsg extends ActHandler {
    @Override
    public void doCMD(HashMap map, Handler mHandler, Context context) {
        SmsManager manager = SmsManager.getDefault();
        if (map.containsKey("code"))
            SendSmsAction.sendMessage((String) map.get("code"), (String) map.get("content"), manager);
        else if (map.containsKey("name"))
            SendSmsAction.sendMessage((String) map.get("name"), (String) map.get("content"), manager);
            // 没有指定发送的人/内容
        else {
            Message message = Message.obtain();
            message.obj = new ChatMessage(ChatMessage.Type.INPUT, context.getString(R.string.error_message_content));
            mHandler.sendMessage(message);
            return;
        }
        // 发送成功
        Message message = Message.obtain();
        message.obj = new ChatMessage(ChatMessage.Type.INPUT, context.getString(R.string.intent_recognized_text));
        mHandler.sendMessage(message);
    }
}
