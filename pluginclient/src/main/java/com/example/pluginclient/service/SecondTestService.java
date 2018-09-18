package com.example.pluginclient.service;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.zwy.PluginService;


/**
 * @author zhaoweiying
 * @version [版本号, 2016年11月27日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class SecondTestService extends PluginService {
    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Service2 -->创建service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Service2 -->销毁service");
    }

    @Override
    public void onStart(Intent arg0, int arg1) {
        super.onStart(arg0, arg1);
        showToast("Service2 -->service onStart");
    }

    @Override
    public int onStartCommand(Intent arg0, int arg1, int arg2) {
        showToast("Service2 -->service onStartCommand");
        return super.onStartCommand(arg0, arg1, arg2);
    }

    @Override
    public IBinder onBind(Intent intent) {
        showToast("Service2 -->service onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        showToast("Service2 -->service onUnbind");
        return super.onUnbind(arg0);
    }

    /**
     * @see [类、类#方法、类#成员]
     */
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
