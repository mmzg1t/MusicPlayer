package com.example.musicplayer;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
public class MainActivity<activity> extends AppCompatActivity implements View.OnClickListener {
    //创建需要用到的控件的变量
    private Button button;
    private TextView tv1,tv2;
    private FragmentManager fm;
    private FragmentTransaction ft;



    protected void onCreate(Bundle savedInstanceState) {
        String[] permissions={Manifest.permission.RECEIVE_SMS};
        int requestCode=999;
        ActivityCompat.requestPermissions(this,permissions,requestCode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定控件
        tv1=(TextView)findViewById(R.id.menu1);
        tv2=(TextView)findViewById(R.id.menu2);
        //设置监听器，固定写法
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);

        fm=getSupportFragmentManager();
        //默认情况下就显示frag1
        ft=fm.beginTransaction();
        ft.replace(R.id.content,new frag1());
        //提交改变的内容
        ft.commit();
    }

    @Override
    //控件的点击事件
    public void onClick(View v){
        ft=fm.beginTransaction();
        //切换选项卡
            if(v.getId()==R.id.menu1)
                ft.replace(R.id.content,new frag1());
            else if (v.getId()==R.id.menu2) {
                ft.replace(R.id.content,new frag2());
            }

        ft.commit();
    }
}

