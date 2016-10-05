package com.novemser.voicetest.handlers;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.novemser.voicetest.utils.ChatMessage;
import com.novemser.voicetest.utils.JsonParser;
import com.novemser.voicetest.utils.NewsResult;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Novemser on 2016/10/5.
 */
public class NewsHandler {

    public static void doCMD(String url, AsyncHttpClient client, final Handler mHandler) {
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ArrayList<NewsResult> results = JsonParser.parseNewsInfo(new String(responseBody));
                // 设置成功
                for (NewsResult result : results) {
                    Message message = Message.obtain();
                    Spanned spText = Html.fromHtml(result.time +
                            " | "+
                            "<a href='" + result.url + "'>"+result.title+"</a>");

                    message.obj =
                            new ChatMessage(ChatMessage.Type.INPUT, spText);

                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
