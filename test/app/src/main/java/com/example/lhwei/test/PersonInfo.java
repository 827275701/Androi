package com.example.lhwei.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

/**
 * Created by lhwei on 2019/2/1.
 */
public class PersonInfo extends Activity {
    Button ret;
    TextView Tname;
    TextView Tjob_number;
    TextView Tsex;
    TextView Tage;
    TextView Thiredate;
    TextView Tbirthday;
    TextView Tmotto;

    String username = null;

    String name = null;    //姓名
    String job_number = null;   //工号
    String sex = null;   //性别
    String age = null;   //年龄
    String hiredate = null;  //入职时间
    String birthday = null; //生日
    String motto = null; //座右铭

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);


        //从mainActivity的意图中获取登录的账号
        Intent  i = this.getIntent();
        username = i.getStringExtra("username");
        System.out.println("personInfo username--------------" + username);

        ret = (Button)findViewById(R.id.Bperson_info_ret);
        Tname = (TextView)findViewById(R.id.Tinfo_name);
        Tjob_number = (TextView)findViewById(R.id.Tinfo_job_number);
        Tsex = (TextView)findViewById(R.id.Tinfo_sex);
        Tage = (TextView)findViewById(R.id.Tinfo_age);
        Thiredate = (TextView)findViewById(R.id.Tinfo_hiredate);
        Tbirthday = (TextView)findViewById(R.id.Tinfo_birthday);
        Tmotto = (TextView)findViewById(R.id.Tinfo_motto);

        ret.setOnClickListener(new ButtonClickListener_PersonInfoRet());

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Tname.setText(name);
                        Tjob_number.setText(job_number);
                        Tsex.setText(sex);
                        Tage.setText(age);
                        Thiredate.setText(hiredate);
                        Tbirthday.setText(birthday);
                        Tmotto.setText(motto);
                        break;
                }
            }
        };

        get_person_info();
    }

    private void get_person_info(){
        //发送HTTP请求去服务其数据库获取用户信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //设置POST请求的body
                    String postBody = "username=" + username;
                    MyHttp myHttp = new MyHttp();
                    Response response = myHttp.connect("person_info", postBody);
                    if (response.isSuccessful()) {  //如果返回200 OK
                        String res_body = response.body().string();
                        System.out.println("==== personInfo start ====");
                        System.out.println(res_body);
                        System.out.println("==== personInfo end ====");
                    } else {
                        Looper.prepare();
                        Toast t = Toast.makeText(getApplicationContext(), "服务器错误!", Toast.LENGTH_SHORT);
                        t.show();
                        Looper.loop();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            //创建有一个 Intent对象，并指定启动程序Iret
            Intent Iret = new Intent();  //创建意图
            Iret.setClass(PersonInfo.this, ChooseSpot.class);
            Iret.putExtra("username", username);
            PersonInfo.this.startActivity(Iret);//启动意图
            PersonInfo.this.finish(); //关闭当前Activity
        } else {
            finish();
            System.exit(0);
        }
    }

    class ButtonClickListener_PersonInfoRet implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //创建有一个 Intent对象，并指定启动程序Iret
            Intent Iret = new Intent();  //创建意图
            Iret.setClass(PersonInfo.this, ChooseSpot.class);
            PersonInfo.this.startActivity(Iret);//启动意图
            PersonInfo.this.finish(); //关闭当前Activity
        }
    }
}