package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


import java.util.Timer;
import java.util.TimerTask;


public class MusicService extends Service {
    //声明一个MediaPlayer引用
    private MediaPlayer player;
    //声明一个计时器引用
    private Timer timer;

    public MusicService() {
    }

    public void onCreate(){
        super.onCreate();
        //player=MediaPlayer.create();
        player=new MediaPlayer();
    }
    //进度条
    public void addTimer(){
        if(timer==null){
            timer=new Timer();
            TimerTask task =new TimerTask() {
                @Override
                public void run() {
                    if(player==null) return;
                    int duration=player.getDuration();//获得歌曲市场
                    int currentPosition=player.getCurrentPosition();//当前时间
                    Message msg=MusicActivity.handler.obtainMessage();//创建消息对象
                    //将音乐菜刀市场和当前进度封装到bundle中
                    Bundle bundle=new Bundle();
                    bundle.putInt("duration",duration);
                    bundle.putInt("currentPosition",currentPosition);
                    //将bundle封装到msg中
                    msg.setData(bundle);
                    //最后将消息发送到主线成的消息队列中
                    MusicActivity.handler.sendMessage(msg);
                }
            };
            //开始计时任务后的5毫秒，第一次执行task任务，以后每500毫秒（0.5s）执行一次
            timer.schedule(task,5,500);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MusicControl();
    }
    //跨进程通信组件Binder
    class MusicControl extends Binder {
        //播放
        public MusicControl() {
        }


        public void initMusic(int i) {
            Log.d("initMusic","initbegin");
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + "music" + i);
            Log.d("initMusic","url:"+uri);
            try {
                // 重置播放器

                player.reset();
                // 加载音频文件
                player.setDataSource(getApplicationContext(), uri);
                player.prepare();
                player.start();
                addTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //父类自带方法
        public void playStart(){
            player.start();
        }
        public void pausePlay(){
            player.pause();
        }
        public void continuePlay(){
            player.start();//继续播放音乐
        }
        //进度条拖动
        public void seekTo(int progress){
            player.seekTo(progress);//设置音乐的播放位置
        }
    }

    // 在 MusicService 类中添加一个取消定时任务的方法
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
    //销毁
    public void onDestroy(){
        super.onDestroy();
        cancelTimer();
        if(player==null) return;
        if(player.isPlaying()) player.stop();//停止
        player.release();//释放资源
        player=null;
    }
}