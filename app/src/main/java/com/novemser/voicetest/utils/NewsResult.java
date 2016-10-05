package com.novemser.voicetest.utils;

/**
 * Created by Novemser on 2016/10/5.
 */
public class NewsResult {
    public String url;
    public String title;
    public String time;

    public NewsResult(String url, String title, String time) {
        this.time = time;
        this.url = url;
        this.title = title;
    }
}
