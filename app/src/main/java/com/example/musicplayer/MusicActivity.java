package com.example.musicplayer;

import static java.lang.Integer.parseInt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MusicActivity";
    //进度条
    private static SeekBar sb;
    private static TextView tv_progress, tv_total, name_song;
    private static ImageView image_cover;
    //动画
    private ObjectAnimator animator;
    private MusicService.MusicControl musicControl;
    private String name;
    private static int position;
    private Intent intent1, intent2;
    ImageView iv_music;
    public int change=0;//记录下标的变化值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //获取bundle消息
        intent1 = getIntent();
        init(intent1);
    }

    private void init(Intent intent) {    //当前位置用点来先显示
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        //总长度
        tv_total = (TextView) findViewById(R.id.tv_total);
        if (tv_progress == null) {
            Log.d(TAG, "tv_progress is null");
        }
        //进度条样式
        sb = (SeekBar) findViewById(R.id.sb);
        if (sb == null) {
            Log.d(TAG, "sb is null");
        }
        //歌曲名控件
        name_song = (TextView) findViewById(R.id.song_name);
        //image_cover=findViewById(R.id.iv_music);
        //设置事件监听
        findViewById(R.id.btn_prev).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        //findViewById(R.id.btn_continue_play).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);

        name = intent.getStringExtra("name");
        name_song.setText(frag1.name[position]);
        Log.d("debug", "name value: " + name);

        //声明并绑定音乐播放器的iv_music控件
        iv_music = findViewById(R.id.iv_music);
        position = intent.getIntExtra("position", 1);

        Log.d("debug", "position value: " + position);

        iv_music.setImageResource(frag1.icons[position]);

        //创建一个从当前Activity跳转到Service的对象
        intent2 = new Intent(this, MusicService.class);
        //conn= new MyServiceConn();//创建服务连接对象
        bindService(intent2, conn, BIND_AUTO_CREATE);

        //为滑动条添加事件监听
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == seekBar.getMax()) {
                    animator.pause();
                }
            }

            //滑动开始时调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //停止时
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止时通过拖动进度条更改播放进度
                int progress = seekBar.getProgress();//获取进度
                musicControl.seekTo(progress);//改变进度
            }
        });

        //ROTATION和0f,360,0f设置为0到360
        animator = ObjectAnimator.ofFloat(iv_music, "rotation", 0f, 360.0f);
        animator.setDuration(20000);//转一圈的时间
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示无限循环

    }

    //handler，类似于线程间通信，获取到信息后转交
    public static Handler handler = new Handler(Looper.getMainLooper()) {
        //在主线程中处理从子线程发来的消息
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();//获取音乐播放进度
            //获取当前进度和总时长

            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            //修改进度条
            sb.setMax(duration);
            sb.setProgress(currentPosition);
            //
            int minute = duration / 1000 / 60;
            int second = duration / 1000 % 60;
            String strMinute = null;
            String strSecond = null;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + "";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + "";
            }
            //显示总时长
            tv_total.setText(strMinute + ":" + strSecond);
            //当前播放市场
            minute = currentPosition / 1000 / 60;
            second = currentPosition / 1000 % 60;
            if (minute < 10) {
                strMinute = "0" + minute;
            } else {
                strMinute = minute + " ";
            }
            if (second < 10) {//如果歌曲中的秒钟小于10
                strSecond = "0" + second;//在秒钟前面加一个0
            } else {
                strSecond = second + " ";
            }
            //显示当前歌曲已经播放的时间
            tv_progress.setText(strMinute + ":" + strSecond);
        }
    };
    //连接服务
    private final ServiceConnection conn = new ServiceConnection() {
        //int position=intent1.getIntExtra("position",1);
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("notions", "linked");
            musicControl = (MusicService.MusicControl) service;
            animator.start();
            musicControl.initMusic(position);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicControl = null;
        }
    };

    //判断是否解绑
    private void unbind(boolean isUnbind) {
        //如果解绑
        if (isUnbind) {
            musicControl.pausePlay();//暂停播放
            unbindService(conn);
            super.onDestroy();
        }
    }

    boolean isPlaying = true;
    int i = position;


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_prev) { // 上一首播放事件
            if((i+change)<1) {
                change=frag1.name.length-1-i;
                musicControl.initMusic(i+change);
                iv_music.setImageResource(frag1.icons[i+change]);
                name_song.setText(frag1.name[i+change]);
                //pause.setVisibility(View.VISIBLE);
                animator.start();
            }
            else {
                change--;
                iv_music.setImageResource(frag1.icons[i+change]);
                name_song.setText(frag1.name[i+change]);
                musicControl.initMusic(i+change);
                //pause.setVisibility(View.VISIBLE);
                animator.start();
            }
        } else if (view.getId() == R.id.btn_pause) { // 播放/暂停按钮事件
            if (isPlaying) {
                musicControl.pausePlay();
                animator.pause();
            } else {
                musicControl.continuePlay();
                animator.start();
            }
            isPlaying = !isPlaying; // 切换播放状态
        } else if (view.getId() == R.id.btn_next) { // 下一首播放事件
            if((i+change)==frag1.name.length-1) {//这里musicName.length-1表示的最后一首歌的下标，即歌曲总数-1
                change=-i;
                musicControl.initMusic(i+change);
                iv_music.setImageResource(frag1.icons[i+change]);
                name_song.setText(frag1.name[i+change]);
                //pause.setVisibility(View.VISIBLE);
                animator.start();
            }
            else {
                change++;
                iv_music.setImageResource(frag1.icons[i+change]);
                name_song.setText(frag1.name[i+change]);
                musicControl.initMusic(i+change);
                //pause.setVisibility(View.VISIBLE);
                animator.start();
            }
        }
    }

}