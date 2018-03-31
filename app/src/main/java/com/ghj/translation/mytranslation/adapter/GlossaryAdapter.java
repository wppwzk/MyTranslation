package com.ghj.translation.mytranslation.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ghj.translation.mytranslation.R;

import java.util.List;

/**
 * Created by YBC on 2018/3/31.
 */

public class GlossaryAdapter extends BaseQuickAdapter<GlossaryItem, BaseViewHolder> {
    public GlossaryAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GlossaryItem item) {
        helper.setText(R.id.listId, item.getFirstLanguage());
        helper.setText(R.id.firstText, item.getFirstLanguage());
        helper.setText(R.id.endText, item.getEndLanguage());
    }
}

