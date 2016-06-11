package com.novemser.voicetest.utils;

import java.util.Date;

/**
 * Created by Novemser on 6/11/2016.
 */
public class AlarmItemSetting {
    private String content;
    private Date time;

    public String getContent() {
        return content;
    }

    public Date getTime() {
        return time;
    }

    public AlarmItemSetting(String content, Date time) {
        this.content = content;
        this.time = time;
    }
}
