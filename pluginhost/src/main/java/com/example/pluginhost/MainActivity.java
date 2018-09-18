package com.example.pluginhost;

import java.io.File;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zwy.ProxyActivity;
import com.zwy.ProxyService;
import com.zwy.utils.PluginConstants;
import com.zwy.utils.PluginUtils;


/**
 * 宿主demo主界面
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MainActivity extends Activity
{

    /**
     * 提示
     */
    private TextView mTipsView = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 设置布局文件
        setContentView(R.layout.activity_main);
        // 初始化界面
        initView();
                

                Intent i = new Intent();
                i.setClassName("com.example.pluginclient", "com.example.pluginclient.SecondActivity");
//                i.setClassName(getPackageName(), i.getComponent().getClassName());
                i.setPackage(getPackageName());
                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                System.out.println(infos == null || infos.isEmpty());
                if (infos != null && infos.size() > 0)
                {
                    System.out.println(infos.get(0).activityInfo);
                }
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                "android.permission.WRITE_EXTERNAL_STORAGE")) {

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("app需要开启权限才能使用此功能")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        } else {

            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                    1);
        }
    }

    /**
     * 初始化界面
     * 
     * @see [类、类#方法、类#成员]
     */
    private void initView()
    {
        mTipsView = (TextView) findViewById(R.id.tips_tv);
    }

    /**
     * launcher插件
     * 
     * @param v
     * @see [类、类#方法、类#成员]
     */
    public void launcherPlugin(View v)
    {
        launcherPluginActivity(null);
    }

    /**
     * launcher插件中指定Activity
     * 
     * @param v
     * @see [类、类#方法、类#成员]
     */
    public void launcherPluginPoint(View v)
    {
        launcherPluginActivity("com.example.pluginclient.SecondActivity");
    }
    /**
     * launcher插件中指定Service
     *
     * @param v
     * @see [类、类#方法、类#成员]
     */
    public void launcherPluginService(View v)
    {
        launcherPluginService("com.example.pluginclient.service.TestService");
    }
    /**
     * 启动插件
     *
     * @param activityName
     * @see [类、类#方法、类#成员]
     */
    public void launcherPluginActivity(String activityName)
    {
        String apkPath = getApkPluginPath();
        // 监测是否有apk插件
        if (TextUtils.isEmpty(apkPath))
        {
            mTipsView.setText("未检测到插件apk,请将打包的apk拷贝到SD卡(如果手机有内置存储，使用内置存储)的zwy目录");
            return;
        }
        // 如果不指定activityName，启动插件的默认Activity
        if (TextUtils.isEmpty(activityName))
        {
            activityName = PluginUtils.getLauncherActivity(this, apkPath);
        }

        Intent i = new Intent(this, ProxyActivity.class);
        i.putExtra(PluginConstants.EXTRA_PLUGIN_APK_PATH, apkPath);
        i.putExtra(PluginConstants.EXTRA_PLUGIN_CLASS, activityName);
        startActivity(i);
    }
    /**
     * 启动插件sevice
     *
     * @param serviceName
     * @see [类、类#方法、类#成员]
     */
    public void launcherPluginService(String serviceName)
    {
        String apkPath = getApkPluginPath();
        // 监测是否有apk插件
        if (TextUtils.isEmpty(apkPath))
        {
            mTipsView.setText("未检测到插件apk,请将打包的apk拷贝到SD卡(如果手机有内置存储，使用内置存储)的zwy目录");
            return;
        }

        Intent i = new Intent(this, ProxyService.class);
        i.putExtra(PluginConstants.EXTRA_PLUGIN_APK_PATH, apkPath);
        i.putExtra(PluginConstants.EXTRA_PLUGIN_CLASS, serviceName);
        startService(i);
    }

    /**
     * 获取SD卡中的apk插件路径
     * 
     * @see [类、类#方法、类#成员]
     */
    private String getApkPluginPath()
    {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "zwy"
                + File.separator);
        if (!file.exists())
        {
            file.mkdirs();
            return "";
        }
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0)
        {
            return "";
        }
        // 检测插件
        for (File plugin : plugins)
        {
            try
            {
                PackageInfo info = PluginUtils.getPackageInfo(this, plugin.getAbsolutePath());
                if (info != null && info.activities != null)
                {
                    return plugin.getAbsolutePath();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }
}
