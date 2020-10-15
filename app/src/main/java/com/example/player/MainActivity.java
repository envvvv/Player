package com.example.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ServiceConnection serviceConnection;
    ServiceMusic.Mybind mybind;
    ProgressBar pb;
    SeekBar sbr;
    TextView tv;
    String ID="0";
    TextView size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar)findViewById(R.id.pb);
        sbr = (SeekBar)findViewById(R.id.sbr);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void PlayonClick(View view) {

        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Play");
        intent.putExtra("ID",ID);
        startService(intent);
        tv.setText("播放状态：正在播放Song"+ID);
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    mybind = (ServiceMusic.Mybind) service;

                    //设置进度条的最大长度
                    int max = mybind.getMusicDuration();
                    pb.setMax(max);
                    sbr.setMax(max);
                    sbr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            mybind.seekTo(progress);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    //连接之后启动子线程设置当前进度
                    new Thread()
                    {
                        public void run()
                        {

                            //改变当前进度条的值
                            //设置当前进度
                            while (true) {
                                pb.setProgress(mybind.getMusicCurrentPosition());
//                                sbr.setProgress(mybind.getMusicCurrentPosition());
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                        }

                    }.start();

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            //以绑定方式连接服务
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        }

    }

    public void PauseonClick(View view) {
        Intent intent = new Intent(this, ServiceMusic.class);
        intent.putExtra("action","Pause");
        startService(intent);
        tv.setText("播放状态：暂停播放");
    }
    public void StoponClick(View view) {

        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Stop");
        startService(intent);
        tv.setText("播放状态：停止播放");
    }

    public void PreviousonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Previous");
        if(ID=="0"){
            ID="3";
            intent.putExtra("ID",ID);
        }
        else{
            ID=String.valueOf(Integer.parseInt(ID)-1);
            intent.putExtra("ID",ID);
        }
        startService(intent);
        tv.setText("播放状态：切换上一首Song"+ID);
    }

    public void NextonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Next");
        if(ID=="3"){
            ID="0";
            intent.putExtra("ID",ID);
        }
        else{
            ID=String.valueOf(Integer.parseInt(ID)+1);
            intent.putExtra("ID",ID);
        }
        startService(intent);
        tv.setText("播放状态：切换下一首Song"+ID);
    }

    public void RandomonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Random");
        Random rand = new Random();
        ID=String.valueOf(rand.nextInt(4));
        intent.putExtra("ID",ID);
        startService(intent);
        tv.setText("播放状态：随机播放");
    }

    public void InOrderonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","InOrder");
        if(ID=="3"){
            ID="0";
            intent.putExtra("ID",ID);
        }
        else{
            ID=String.valueOf(Integer.parseInt(ID)+1);
            intent.putExtra("ID",ID);
        }
        intent.putExtra("ID",ID);
        startService(intent);
        tv.setText("播放状态：按序播放");
    }
}