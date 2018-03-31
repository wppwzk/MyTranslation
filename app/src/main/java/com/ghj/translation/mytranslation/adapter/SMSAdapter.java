package com.ghj.translation.mytranslation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ghj.translation.mytranslation.R;

import java.util.List;

/**
 * Created by YBC on 2018/3/31.
 */

public class SMSAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {
    public SMSAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItem item) {
        helper.setText(R.id.messageTime, item.getTime());
        helper.setText(R.id.messagePhone, item.getNumber());
        helper.setText(R.id.messageText, item.getMessage());
    }
}
