package com.example.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class ServiceMusic extends Service {
    private MediaPlayer mediaPlayer;
    public ServiceMusic() {
    }

    public class Mybind extends Binder
    {
        //获取歌曲长度
        public int getMusicDuration()
        {
            int length = 0;
            if (mediaPlayer != null)
            {
                length = mediaPlayer.getDuration();
            }

            return length;
        }
        //获取当前播放进度
        public int getMusicCurrentPosition()
        {
            int cp = 0;
            if (mediaPlayer != null)
            {
                cp = mediaPlayer.getCurrentPosition();

            }

            return cp;
        }

        public void seekTo(int position)
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.seekTo(position);
            }
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return   new Mybind();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        //获取意图传递的信息
        String action = intent.getStringExtra("action");
        String ID=intent.getStringExtra("ID");
        switch (action)
        {
            case "Play":
                if (mediaPlayer == null)
                {
                    switch (ID){
                        case "0":
                            mediaPlayer = MediaPlayer.create(this,R.raw.song0);
                            break;
                        case "1":
                            mediaPlayer = MediaPlayer.create(this,R.raw.song1);
                            break;
                        case "2":
                            mediaPlayer = MediaPlayer.create(this,R.raw.song2);
                            break;
                        case "3":
                            mediaPlayer = MediaPlayer.create(this,R.raw.song3);
                            break;
                    }
                    mediaPlayer.start();
                }

                break;
            case "Stop":
            case "Previous":
            case "Next":
            case "Random":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                break;
            case "Pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                break;
        }
        return super.onStartCommand(intent, flags, startId);
}
}

