package com.ghj.translation.mytranslation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ghj.translation.mytranslation.adapter.GlossaryAdapter;
import com.ghj.translation.mytranslation.adapter.GlossaryItem;
import com.ghj.translation.mytranslation.database.DatabaseHelper;
import com.ghj.translation.mytranslation.database.DatabaseOperation;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class GlossaryActivity extends AppCompatActivity {
    private List<GlossaryItem> list = new ArrayList<>();
    private DatabaseOperation databaseOperation;
    private GlossaryAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("生词本");
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        initList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initList() {
        databaseOperation = new DatabaseOperation(GlossaryActivity.this);
        list = databaseOperation.getWordList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GlossaryActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(GlossaryActivity.this)
                .margin(20, 20).build());
        recyclerAdapter = new GlossaryAdapter(R.layout.glossary_item_layout, list);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {


            }
        });
        recyclerAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                return false;
            }
        });
    }

}
