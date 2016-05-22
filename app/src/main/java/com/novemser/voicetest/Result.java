package com.novemser.voicetest;

/**
 * Created by Novemser on 5/22/2016.
 */
import java.util.ArrayList;
import java.util.List;

public class Result {
    private int code;
    private String text;
    private ArrayList<List> list;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public ArrayList<List> getList() {
        return list;
    }
    public void setList(ArrayList<List> list) {
        this.list = list;
    }
    public Result(int code, String text, ArrayList<List> list) {
        super();
        this.code = code;
        this.text = text;
        this.list = list;
    }
    public Result() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    public String toString() {
        return "Result [code=" + code + ", text=" + text + ", list=" + list
                + "]";
    }


}
