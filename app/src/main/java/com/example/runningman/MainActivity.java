package com.example.runningman;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.example.runningman.fragment.BlankFragment;
import com.example.runningman.fragment.ChatFragment;
import com.example.runningman.fragment.ContactFragment;
import com.example.runningman.fragment.FindFragment;
import com.example.runningman.fragment.ProfileFragment;
import com.example.runningman.view.TabView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SDKReceiver mReceiver;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    SensorManager mSensorManager;//管理器实例
    Sensor stepCounter;//传感器
    float mSteps = 0;//步数
    TextView steps;//显示步数
    TextView time;//显示时间



    @BindView(R.id.tab_weixin)
    TabView mTabWeixin;

    @BindView(R.id.tab_contact)
    TabView mTabContact;

    @BindView(R.id.tab_find)
    TabView mTabFind;

    @BindView(R.id.tab_profile)
    TabView mTabProfile;

    private List<TabView> mTabViews = new ArrayList<>();

    private static final int INDEX_WEIXIN = 0;
    private static final int INDEX_CONTACT = 1;
    private static final int INDEX_FIND = 2;
    private static final int INDEX_PROFILE = 3;
    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MainActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(MainActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();   // 获取Fragment
        FragmentTransaction ft = fm.beginTransaction(); // 开启一个事务
        Fragment f = new BlankFragment(); //为Fragment初始化
        ft.replace(R.id.run_content, f); //替换Fragment
        ft.commit();
        setContentView(R.layout.activity_main);

        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);

        ButterKnife.bind(this);

        mTabViews.add(mTabWeixin);

        mTabViews.add(mTabContact);
        mTabViews.add(mTabFind);
        mTabViews.add(mTabProfile);
        updateCurrentTab(INDEX_WEIXIN);

// 获取SensorManager管理器实例
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // getSensorList用于列出设备支持的所有sensor列表
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.i("yyy","Sensor size:"+sensorList.size());
        for (Sensor sensor : sensorList) {
            Log.i("yyy","Supported Sensor: "+sensor.getName());
        }

        steps = (TextView)findViewById(R.id.steps);
        time = (TextView)findViewById(R.id.time1);
        // 获取计步器sensor
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepCounter != null){
            // 如果sensor找到，则注册监听器
            mSensorManager.registerListener(this,stepCounter,1000000);
        }
        else{
            Log.e("yyy","no step counter sensor found");
        }




        new TimeThread().start();











    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        mSteps = event.values[0];
        steps.setText("你已经走了"+String.valueOf((int)mSteps)+"步");
        Log.e("yyy","no step counter sensor foundddddddd");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateCurrentTab(int index) {
        for (int i = 0; i < mTabViews.size(); i++) {
            if (index == i) {
                mTabViews.get(i).setXPercentage(1);
            } else {
                mTabViews.get(i).setXPercentage(0);
            }
        }
    }

    @OnClick({R.id.tab_weixin, R.id.tab_contact, R.id.tab_find, R.id.tab_profile})
    public void onClickTab(View v) {
        FragmentManager fm = getSupportFragmentManager();   // 获取Fragment
        FragmentTransaction ft = fm.beginTransaction(); // 开启一个事务
        Fragment f = null; //为Fragment初始化

        switch (v.getId()) {
            case R.id.tab_weixin:
                f = new BlankFragment();
                mViewPager.setCurrentItem(INDEX_WEIXIN, false);
                updateCurrentTab(INDEX_WEIXIN);
                break;
            case R.id.tab_contact:
                f = new BlankFragment();
                mViewPager.setCurrentItem(INDEX_CONTACT, false);
                updateCurrentTab(INDEX_CONTACT);
                break;

            case R.id.tab_find:
                mViewPager.setCurrentItem(INDEX_FIND, false);
                updateCurrentTab(INDEX_FIND);
                break;

            case R.id.tab_profile:
                mViewPager.setCurrentItem(INDEX_PROFILE, false);
                updateCurrentTab(INDEX_PROFILE);
                break;
        }
        ft.replace(R.id.run_content, f); //替换Fragment
        ft.commit(); //提交事务
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();//获取系统时间
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);//时间显示格式
                    time.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
