package com.example.player;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ServiceConnection serviceConnection;
    ServiceMusic.Mybind mybind;
    ProgressBar pb;
    SeekBar sbr;
    TextView tv;
    ListView listView;
    String ID="0";
    final int  RANDOM=1;
    final int  INORDER=2;
    int TYPE=INORDER;
    List<Map<String ,String>> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = (ProgressBar)findViewById(R.id.pb);
        sbr = (SeekBar)findViewById(R.id.sbr);
        tv=(TextView)findViewById(R.id.tv);
        listView=(ListView)findViewById(R.id.MusicList);

        for(int i=0;i<=2;i++){
        Map<String,String> map=new HashMap<>();
        map.put("Id",String.valueOf(i));
        map.put("Name","Song"+String.valueOf(i));
        list.add(map);
        }
        setListViewAdapter(list);
        registerForContextMenu(listView);
        final Intent intent = new Intent(this,ServiceMusic.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ID=String.valueOf(i);
                intent.putExtra("action","PlAY");
                intent.putExtra("ID",ID);
                startService(intent);
                tv.setText("播放状态：正在播放Song"+ID);
            }
        });
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getMenuInflater().inflate(R.menu.contextmenu, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        TextView Id = null;

        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;

        switch (item.getItemId()) {
            case R.id.delete:
                //删除单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                itemView = info.targetView;
                Id = (TextView) itemView.findViewById(R.id.id);
                if (Id != null) {
                    String strId = Id.getText().toString();
                        onDeleteDialog(strId);
                }
                break;
        }
        return true;
    }

    private void onDeleteDialog(String strId) {
       for(int i=0;i<list.size();i++){
           if(list.get(i).containsValue(strId)){
               list.remove(i);
           }
       }
       setListViewAdapter(list);
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
//                            mybind.seekTo(progress);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mybind.seekTo(sbr.getProgress());

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
                                sbr.setProgress(mybind.getMusicCurrentPosition());
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

    public void PreviousonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Previous");
        if(TYPE==RANDOM){
            Random rand = new Random();
            ID = String.valueOf(rand.nextInt(4));
        }
        else {
            if(Integer.parseInt(ID)<=0)
                ID=String.valueOf(list.size()-1);
            else
                ID=String.valueOf(Integer.parseInt(ID)-1);
        }
        intent.putExtra("ID",ID);
        tv.setText("播放状态：切换上一首Song"+ID);
        startService(intent);
        tv.setText("播放状态：正在播放Song"+ID);
    }

    public void NextonClick(View view) {
        Intent intent = new Intent(this,ServiceMusic.class);
        intent.putExtra("action","Next");
        if(TYPE==RANDOM){
            Random rand = new Random();
            ID = String.valueOf(rand.nextInt(list.size()));
        }
        else {
            if(Integer.parseInt(ID)>=list.size()-1)
                ID="0";
            else
                ID=String.valueOf(Integer.parseInt(ID)+1);
        }
        intent.putExtra("ID",ID);
        tv.setText("播放状态：切换下一首Song"+ID);
        startService(intent);
        tv.setText("播放状态：正在播放Song"+ID);
    }

//    public void RandomonClick(View view) {
//        Intent intent = new Intent(this,ServiceMusic.class);
//        intent.putExtra("action","Random");
//        intent.putExtra("ID",ID);
//        startService(intent);
//        TYPE=RANDOM;
//        tv.setText("播放状态：随机播放");
//        tv.setText("播放状态：正在播放Song"+ID);
//    }

//    public void InOrderonClick(View view) {
//        Intent intent = new Intent(this,ServiceMusic.class);
//        intent.putExtra("action","InOrder");
//        intent.putExtra("ID",ID);
//        startService(intent);
//        TYPE=INORDER;
//        tv.setText("播放状态：按序播放");
//        tv.setText("播放状态：正在播放Song"+ID);
//    }
    private void setListViewAdapter(List<Map<String, String>> list) {

        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.item,
                new String[] { "Id","Name" }, new int[] { R.id.id,R.id.name });
        listView.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                addDialog();
                break;
            case R.id.Random:
                TYPE=RANDOM;
                tv.setText("播放状态：随机播放");
                tv.setText("播放状态：正在播放Song"+ID);
                break;
            case R.id.InOrder:
                TYPE=INORDER;
                tv.setText("播放状态：按序播放");
                tv.setText("播放状态：正在播放Song"+ID);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View viewDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.adddialog, null, false);
        builder.setTitle("添加歌曲").setView(viewDialog)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText SongID=(EditText) viewDialog.findViewById(R.id.SongID);
                        int d=0;
                        for(int t=0;t<list.size();t++){
                            if(list.get(t).containsValue(SongID.getText().toString())){
                                d=1;
                                Toast.makeText(MainActivity.this,"已存在Song"+SongID.getText().toString(),Toast.LENGTH_LONG).show();}
                        }
                        if(d==0) {
                            Map<String, String> map = new HashMap<>();
                            map.put("Id", SongID.getText().toString());
                            map.put("Name", "Song" + SongID.getText().toString());
                            list.add(map);
                            setListViewAdapter(list);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
}