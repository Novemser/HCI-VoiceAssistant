package com.novemser.voicetest.utils;

/**
 * Created by Novemser on 5/24/2016.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import java.sql.SQLClientInfoException;
import java.util.HashMap;

/**
 * Json结果解析类
 */
public class JsonParser {

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("【置信度】" + obj.getInt("sc"));
                    ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

    public static String parseLocalGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("没有匹配结果.");
                        return ret.toString();
                    }
                    ret.append("【结果】" + obj.getString("w"));
                    ret.append("\n");
                }
            }
            ret.append("【置信度】" + joResult.optInt("sc"));

        } catch (Exception e) {
            e.printStackTrace();
            ret.append("没有匹配结果.");
        }
        return ret.toString();
    }

    public static HashMap parseSemanticResult(String json) {
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            if (json == null || json.equals("") || !joResult.has("semantic")) {
                map.put("semanticNull", "true");
                return map;
            }

            if (joResult.has("operation"))
                map.put("operation", joResult.getString("operation"));

            if (joResult.has("message"))
                map.put("message", joResult.getString("service"));

            JSONObject semantic = joResult.getJSONObject("semantic");
            if (semantic.has("slots")) {
                JSONObject slots = semantic.getJSONObject("slots");

                if (slots.has("code"))
                    map.put("code", slots.getString("code"));

                if (slots.has("name"))
                    map.put("name", slots.getString("name"));

                if (slots.has("content"))
                    map.put("content", slots.getString("content"));

                if (slots.has("datetime")) {
                    JSONObject dateTime = slots.getJSONObject("datetime");

                    if (dateTime.has("dateOrig"))
                        map.put("dateOrig", dateTime.getString("dateOrig"));

                    if (dateTime.has("time"))
                        map.put("time", dateTime.getString("time"));

                    if (dateTime.has("timeOrig"))
                        map.put("timeOrig", dateTime.getString("timeOrig"));

                    if (dateTime.has("date"))
                        map.put("date", dateTime.getString("date"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
