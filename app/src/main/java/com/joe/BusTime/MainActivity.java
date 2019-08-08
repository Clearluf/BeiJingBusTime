package com.joe.BusTime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends AppCompatActivity {

    String linename;
    int directionIndex;
    String directionID;
    int stationIndex;
    String result;
    ArrayList<String> directionlist;
    ArrayList<String> stationlist;
    Spinner directionSpinner = null;
    Spinner stationSpinner = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //directionSpinner=(Spinner)findViewById(R.id.spinner);
        stationSpinner = (Spinner) findViewById(R.id.spinner2);
        String[] lines =getResources().getStringArray(R.array.line_array);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);//找到相应的控件
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, lines);//配置Adaptor
        autoCompleteTextView.setAdapter(adapter);

    }

    //检查线路名输入
    public void checkLine(View v) throws InterruptedException {
        AutoCompleteTextView act=findViewById(R.id.autoCompleteTextView);
        linename = act.getText().toString();
        //System.out.println(linename);
        // Android 4.0 之后不能在主线程中请求HTTP请求

        //CountDownLatch 等待子线程执行完毕
        final CountDownLatch cd=new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                directionlist = new BusUtil().selectLine(linename);
                cd.countDown();
            }
        }).start();
        cd.await();
        directionSpinner = (Spinner) findViewById(R.id.spinner);
        int dsize = directionlist.size();
        String[] dirc = new String[dsize];
        for (int i = 0; i < dsize; i++) {
            dirc[i] = directionlist.get(i).split("[()]")[1];
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dirc);
        directionSpinner.setAdapter(adapter);
    }

    //检查方向选择
    public void checkDirection(View v) throws InterruptedException{
        directionIndex = directionSpinner.getSelectedItemPosition();
        directionID = directionlist.get(directionIndex).split(" ")[1];
        final CountDownLatch cd=new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                stationlist = new BusUtil().getStationList(linename, directionID);
                cd.countDown();
            }
        }).start();
        cd.await();
        stationSpinner = (Spinner) findViewById(R.id.spinner2);
        int ssize = stationlist.size();
        String[] sta = new String[ssize];
        for (int i = 0; i < ssize; i++) {
            sta[i] = stationlist.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sta);
        stationSpinner.setAdapter(adapter);
    }

    //检查站点选择
    public void checkStation(View v) throws InterruptedException{
        stationIndex = stationSpinner.getSelectedItemPosition();
        final CountDownLatch cd=new CountDownLatch(1);
        new Thread(new Runnable(){
            @Override
            public void run() {
                result = new BusUtil().getArrivingTime(linename, directionID, stationIndex);
                cd.countDown();
            }
            }).start();
        cd.await();
        TextView txv=findViewById(R.id.resultView);
        txv.setText(result);

    }

}
