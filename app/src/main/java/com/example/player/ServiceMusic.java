package com.example.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ServiceMusic extends Service {
    private MediaPlayer mediaPlayer;
    final int  RANDOM=1;
    final int  INORDER=2;
    int TYPE=RANDOM;
    String ID;
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

    public void play(String ID){
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
                case "4":
                    mediaPlayer = MediaPlayer.create(this,R.raw.song4);
                    break;
                case "5":
                    mediaPlayer = MediaPlayer.create(this,R.raw.song5);
                    break;
            }
            mediaPlayer.start();
    }

    }
//public String next(String ID){
//    if(TYPE==RANDOM){
//        Random rand = new Random();
//        ID = String.valueOf(rand.nextInt(4));
//    }
//    else {
//        if(Integer.parseInt(ID)>=3)
//            ID="0";
//        else
//            ID=String.valueOf(Integer.parseInt(ID)+1);
//    }
//    return ID;
//}

//public String previous(String ID){
//    if(TYPE==RANDOM){
//        Random rand = new Random();
//        ID = String.valueOf(rand.nextInt(4));
//        play(ID);
//    }
//    else {
//        if(Integer.parseInt(ID)<=0)
//        ID="3";
//        else
//        ID=String.valueOf(Integer.parseInt(ID)-1);
//    }
//    return ID;
//}
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //获取意图传递的信息
        String action = intent.getStringExtra("action");
        ID=intent.getStringExtra("ID");
        switch (action)
        {
            case "Play":
                    play(ID);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer arg0) {
                            mediaPlayer.reset();
                            if(TYPE==RANDOM){
                                Random rand = new Random();
                                ID = String.valueOf(rand.nextInt(4));
                                play(ID);
                            }
                            else {
                                play(ID);
                            }
                        }
                    });
                break;
            case "Previous":
            case "Next":
            case "PlAY":
                if (mediaPlayer !=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    play(ID);
                }
                break;
            //                    ID=next(ID);
            case "Pause":
                if (mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                break;

//            case "Random":
//                TYPE=RANDOM;
//            case "InOrder":
//                TYPE=INORDER;
        }
        return super.onStartCommand(intent, flags, startId);
}
}

