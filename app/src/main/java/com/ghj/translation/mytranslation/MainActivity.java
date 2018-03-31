package com.ghj.translation.mytranslation;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ghj.translation.mytranslation.database.DatabaseHelper;
import com.ghj.translation.mytranslation.database.DatabaseOperation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Button translationButton, saveButton, copyButton, clearButton;
    private AlertDialog alertDialog1;
    private CardView cardView;
    private String theLastWorld;
    private SharedPreferences sp;
    private OkHttpClient client;
    private String failReason;
    private EditText firstEdit;
    private TextView endEdit, fromTextChoose, toTextChoose;
    private DatabaseOperation databaseOperation;
    private String lastText;
    private DatabaseHelper databaseHelper;
    private boolean saveBut = false;
    private final int RETURN_OK = 0;
    private final int RETURN_FAIL = 1;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case RETURN_OK:
                    cardView.setVisibility(View.VISIBLE);
                    endEdit.setText(lastText);
                    break;
                case RETURN_FAIL:
                    cardView.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, failReason, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseHelper = new DatabaseHelper(this, "NoteStore.db", null, 11);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        cardView = findViewById(R.id.endTranslationLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        firstEdit = findViewById(R.id.firstLanguageEdit);
        endEdit = findViewById(R.id.endLanguageEdit);
        fromTextChoose = findViewById(R.id.firstLanguageChoose);
        toTextChoose = findViewById(R.id.endLanguageChoose);
        translationButton = findViewById(R.id.translationButton);
        saveButton = findViewById(R.id.collectionButton);
        copyButton = findViewById(R.id.copyButton);
        clearButton = findViewById(R.id.clearInput);
        sp = getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("from", "zh");
        edit.putString("to", "en");
        edit.apply();
        EventBus.getDefault().register(this);
        /*Intent intent=getIntent();
        String message = intent.getStringExtra("sms");

        if (message != null ) {
            Log.e("mes",message);
           firstEdit.setText("");
           firstEdit.setText(message);
           translate();
        }*/
        databaseOperation = new DatabaseOperation(MainActivity.this);
        fromTextChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromListAlertDialog(view);
            }
        });
        toTextChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToListAlertDialog(view);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!saveBut) {
                    saveButton.setBackgroundResource(R.drawable.favority_ok);
                    databaseOperation.insert_db(theLastWorld, endEdit.getText() + "");
                    Toast.makeText(MainActivity.this, "已添加到生词本", Toast.LENGTH_SHORT).show();
                    saveBut = true;
                } else {
                    saveButton.setBackgroundResource(R.drawable.favorite);
                    databaseOperation.delete_db(theLastWorld);
                    Toast.makeText(MainActivity.this, "已从生词本移除", Toast.LENGTH_SHORT).show();
                    saveBut = false;
                }
            }
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", endEdit.getText() + "");
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
        translationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 获取软键盘的显示状态
                boolean isOpen = imm.isActive();
                if (isOpen)
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                translate();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstEdit.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void translateSMS(MessageEvent messageEvent) {
        Log.e("mes", messageEvent.getMessage());
        firstEdit.setText("");
        firstEdit.setText(messageEvent.getMessage());
        translate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, GlossaryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            getSMSPermission();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 信息列表提示框
    public void showFromListAlertDialog(View view) {
        final String[] items = {"中文", "英语", "日语", "韩语", "法语", "西班牙语", "俄语", "德语"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        //final String[] language = new String[1];
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                //Toast.makeText(MainActivity.this, items[index], Toast.LENGTH_SHORT).show();
                fromTextChoose.setText(items[index]);
                sp = getSharedPreferences("language", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("from", items[index]);
                edit.apply();
                alertDialog1.dismiss();

            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
        //return language[0];
    }

    public void showToListAlertDialog(View view) {
        final String[] items = {"中文", "英语", "日语", "韩语", "法语", "西班牙语", "俄语", "德语"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        //final String[] language = new String[1];
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int index) {
                //Toast.makeText(MainActivity.this, items[index], Toast.LENGTH_SHORT).show();
                toTextChoose.setText(items[index]);
                sp = getSharedPreferences("language", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("to", items[index]);
                edit.apply();
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
        //return language[0];
    }

    private void translate() {
        saveBut = false;
        saveButton.setBackgroundResource(R.drawable.favorite);
        endEdit.setText("");
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        FormBody.Builder builderBody = new FormBody.Builder();
        String q = firstEdit.getText() + "";
        String from = getFromLanguage(sp.getString("from", "zh"));
        String to = getToLanguage(sp.getString("to", "en"));
        Log.e(q, from + to + "");
        String appid = "20171001000086134";
        String salt = "1435660288";
        String kyes = "WGmpm7QECJnrdsjVH3Ec1";
        String sign = stringToMD5(appid + q + salt + kyes);
        builderBody.add("q", q).add("from", from).add("to", to).add("appid", appid)
                .add("salt", salt).add("sign", sign);
        Log.e("body", sign + "");
        RequestBody formBody = builderBody.build();
        Request.Builder builder = new Request.Builder();
        final Request request = builder.url("http://api.fanyi.baidu.com/api/trans/vip/translate").post(formBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failReason = "翻译出错";
                Message msg = handler.obtainMessage();
                msg.what = RETURN_FAIL;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.e("onResponse", res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if (res.contains("error_code")) {
                        failReason = jsonObject.getString("error_msg");
                        Message msg = handler.obtainMessage();
                        msg.what = RETURN_FAIL;
                        handler.sendMessage(msg);
                    }
                    if (res.contains("trans_result")) {
                        theLastWorld = firstEdit.getText() + "";
                        String status = jsonObject.getString("trans_result");
                        JSONArray jsonArray = new JSONArray(status);
                        JSONObject text = jsonArray.getJSONObject(0);
                        lastText = text.getString("dst");
                        Message msg = handler.obtainMessage();
                        msg.what = RETURN_OK;
                        handler.sendMessage(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    failReason = "翻译出错";
                    Message msg = handler.obtainMessage();
                    msg.what = RETURN_FAIL;
                    handler.sendMessage(msg);
                }

            }
        });


    }

    /**
     * 将字符串转成MD5值
     *
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    private boolean getSMSPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_SMS}, 1);
        } else {
            call();
        }
        return false;
    }

    private void call() {
        Intent intent = new Intent(this, TranslateMessageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call();
                } else {
                    Toast.makeText(MainActivity.this, "请授予短信权限才能使用此功能", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private String getFromLanguage(String text) {
        String languageCode = "";
        switch (text) {
            case "中文":
                languageCode = "zh";
                break;
            case "英语":
                languageCode = "en";
                break;
            case "日语":
                languageCode = "jp";
                break;
            case "韩语":
                languageCode = "kor";
                break;
            case "法语":
                languageCode = "fra";
                break;
            case "西班牙语":
                languageCode = "spa";
                break;
            case "俄语":
                languageCode = "ru";
                break;
            case "德语":
                languageCode = "de";
                break;
            default:
                languageCode = "zh";
                break;
        }
        return languageCode;
    }

    private String getToLanguage(String text) {
        String languageCode = "";
        switch (text) {
            case "中文":
                languageCode = "zh";
                break;
            case "英语":
                languageCode = "en";
                break;
            case "日语":
                languageCode = "jp";
                break;
            case "韩语":
                languageCode = "kor";
                break;
            case "法语":
                languageCode = "fra";
                break;
            case "西班牙语":
                languageCode = "spa";
                break;
            case "俄语":
                languageCode = "ru";
                break;
            case "德语":
                languageCode = "de";
                break;
            default:
                languageCode = "en";
                break;
        }
        return languageCode;
    }
}
