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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
                Collections.sort(results, new Comparator<NewsResult>() {
                    @Override
                    public int compare(NewsResult lhs, NewsResult rhs) {
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            Date time1 = parser.parse(lhs.time);
                            Date time2 = parser.parse(rhs.time);
                            return time1.compareTo(time2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
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
