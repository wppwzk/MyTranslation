package com.ghj.translation.mytranslation;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ghj.translation.mytranslation.adapter.GlossaryAdapter;
import com.ghj.translation.mytranslation.adapter.GlossaryItem;
import com.ghj.translation.mytranslation.adapter.MessageItem;
import com.ghj.translation.mytranslation.adapter.SMSAdapter;
import com.ghj.translation.mytranslation.database.DatabaseOperation;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslateMessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MessageItem> list = new ArrayList<>();
    private SMSAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_message);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("短信翻译");
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        getSmsFromPhone();
        initList();
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TranslateMessageActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new SMSAdapter(R.layout.message_list_layout, list);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               /* Intent intent=new Intent(TranslateMessageActivity.this,MainActivity.class);
                intent.putExtra("sms",list.get(position).getMessage());
                startActivity(intent);*/
                EventBus.getDefault().post(new MessageEvent(list.get(position).getMessage()));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc", "************cur == null");
            return;
        }
        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String date = cur.getString(cur.getColumnIndex("date"));//date
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date dt = new Date(Long.parseLong(date));
            String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
            MessageItem item=new MessageItem(body,number,sDateTime);
            list.add(item);
        }
    }
}
