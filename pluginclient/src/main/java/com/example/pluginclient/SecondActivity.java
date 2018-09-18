package com.example.pluginclient;

import static com.example.pluginclient.MainActivity.ACTION_PLUGIN_TEST_BROAD_CAST;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.example.pluginclient.fragments.TestFragment;
import com.example.pluginclient.service.TestService;
import com.zwy.PluginActivity;


/**
 * 其他功能测试界面
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月21日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class SecondActivity extends PluginActivity implements View.OnClickListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Fragment webViewFragment = new TestFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, webViewFragment).commit();
        findViewById(R.id.send_broadcast_bt).setOnClickListener(this);
        findViewById(R.id.start_service_bt).setOnClickListener(this);
        findViewById(R.id.stop_service_bt).setOnClickListener(this);
        findViewById(R.id.bind_service_bt).setOnClickListener(this);
        findViewById(R.id.unBind_service_bt).setOnClickListener(this);
        if (getIntent().hasExtra("extra_test_serializable"))
        {
            Toast.makeText(getApplicationContext(), getIntent().getSerializableExtra("extra_test_serializable") + "",
                    Toast.LENGTH_SHORT).show();
        }
        if (getIntent().hasExtra("extra_test_parcel"))
        {
            Toast.makeText(getApplicationContext(), getIntent().getParcelableExtra("extra_test_parcel") + "",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        Toast.makeText(getApplicationContext(), "这5个等待后续实现",
                Toast.LENGTH_SHORT).show();
        if (v == null)
        {
            return;
        }
        switch (v.getId())
        {
            case R.id.send_broadcast_bt: // 发送广播
                sendBroadcast(new Intent(ACTION_PLUGIN_TEST_BROAD_CAST));

                break;
            case R.id.start_service_bt: // 启动插件中的service
                try {
                    Intent i = new Intent(this, TestService.class);
                    startService(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_service_bt: // 停止service
                try {
                    Intent j = new Intent(this, TestService.class);
                    stopService(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bind_service_bt: // 启动插件中的service
                try {
                    Intent k = new Intent(this, TestService.class);
                    bindService(k, serviceConnection, Context.BIND_AUTO_CREATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.unBind_service_bt: // 停止service
                try {
                    unbindService(serviceConnection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println(" onServiceDisconnected ----" + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("onServiceConnected ----" + name);
        }
    };

    @Override
    public void finish()
    {
        Intent i = new Intent();
        i.putExtra("test", "测试");
        setResult(RESULT_OK, i);
        super.finish();
    }

}
