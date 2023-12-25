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
    private MyReceiver2 myReceiver2;
    String actionname="666";
    Intent intent=new Intent(actionname);
    //myReceiver2 = new MyReceiver2();
    //IntentFilter filter=new IntentFilter();
    //filter.addAction(actionname);

     //registerReceiver( myReceiver2 , filter);

    //button.setOnClickListener(){
    //    public void onclick(View view){
    //        sendBroadcast(intent);
    //    }
    //}
    // Fragment初始化函数.fm可以理解为Fragment显示的管理者，ft就是它的改变者
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();

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
        //若是继承FragmentActivity，fm=getFragmentManger();

        //默认情况下就显示frag1
        ft.replace(R.id.content,new frag1());
        //提交改变的内容
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 999) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了短信权限，可以执行读取短信的操作
                // 执行读取短信的逻辑...
            } else {
                // 用户拒绝了短信权限，你可以根据需要进行适当的处理
            }
        }
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

