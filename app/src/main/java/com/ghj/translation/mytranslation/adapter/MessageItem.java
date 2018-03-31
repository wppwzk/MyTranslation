package com.ghj.translation.mytranslation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ghj.translation.mytranslation.R;

import java.util.List;

/**
 * Created by YBC on 2018/3/31.
 */

public class MessageItem {
    String message;
    String number;
    String time;

    public MessageItem(String message, String number, String time) {
        this.time = time;
        this.message = message;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}