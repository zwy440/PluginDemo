package com.example.pluginclient.fragments;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.pluginclient.R;
import com.example.pluginclient.ThirdActivity;
import com.zwy.PluginActivity;
import com.zwy.PluginFragment;
import com.zwy.utils.PluginConstants;
import com.zwy.utils.PluginUtils;


/**
 * webView fragment
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class TestFragment extends PluginFragment
{

    private View mGoToProxyBt = null;

    private View mGoToPlguinBt1 = null;
    private View mGoToPlguinBt2 = null;
    private View mGoToPlguinBt3 = null;
    @Override
    public View onCreateView(LayoutInflater inflater, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_webview, null);
        mGoToProxyBt = v.findViewById(R.id.launcher_proxy_activity_bt);
        mGoToPlguinBt1 = v.findViewById(R.id.launcher_plugin1_activity_bt);
        mGoToPlguinBt2 = v.findViewById(R.id.launcher_plugin2_activity_bt);
        mGoToPlguinBt3 = v.findViewById(R.id.launcher_plugin3_activity_bt);
        System.out.println(mCotext.getClass().getName());
        int id = mCotext.getResources().getIdentifier("fragment_webview", "layout", mCotext.getPackageName());
        System.out.println(id + "");

        return v;
    }

    /**
     * @param savedInstanceState
     */
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView()
    {
        mGoToProxyBt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 调起宿主的某个Activity
                launcherProxyActivity();
            }
        });
        mGoToPlguinBt1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launcherPluginActivity(1);
            }
        });
        mGoToPlguinBt2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launcherPluginActivity(2);
            }
        });

        mGoToPlguinBt3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launcherPluginActivity(3);
            }
        });
    }

    /**
     * 调起宿主的Activity
     * 
     * @see [类、类#方法、类#成员]
     */
    public void launcherProxyActivity()
    {
        try
        {
            Intent i = new Intent();
            i.setClassName(getActivity(), "com.example.pluginhost.SecondActivity");
            startActivity(i);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(mCotext, mCotext.getString(R.string.cantOpen), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * 调起插件中的Activity
     * 
     * @see [类、类#方法、类#成员]
     */
    public void launcherPluginActivity(int type)
    {
        try
        {
            /**
             * 遇到两种跳转方式，可以做为面试时说的难点
             * 面试阿里必备 为什么要设置包名
             * Intent默认为什么是插件包名
             */


            if(type == 1){
                Intent i = new Intent();
                i.setComponent(new ComponentName("com.example.pluginhost","com.zwy.ProxyActivity")); //第一种
                  i.putExtra(PluginConstants.EXTRA_PLUGIN_APK_PATH, getApkPluginPath()); //第一种
                 i.putExtra(PluginConstants.EXTRA_PLUGIN_CLASS, "com.example.pluginclient.ThirdActivity"); //第一种
                mCotext.startActivity(i);  //第一种
            }else if(type == 2){
                Intent i = new Intent(getActivity(),ThirdActivity.class);
                //Intent temp = new Intent();
                //i.setClassName( mCotext,ThirdActivity.class);为啥host增加supportv4也跳转但不知道跳转到哪里
                //temp.setClassName( mCotext,"com.example.pluginclient.ThirdActivity");
                this.startActivity(i);//第二种
            }else{
                Intent i = new Intent(mCotext,ThirdActivity.class);
               // Intent temp = new Intent();
                //i.setClassName( mCotext,ThirdActivity.class);为啥buke
              //  Intent i = new Intent(mCotext,ThirdActivity.class);
               // temp.setClassName( getActivity(),"com.example.pluginclient.ThirdActivity");//getActivity()和mCotext差别大了
               // temp.setClassName( mCotext,"com.example.pluginclient.ThirdActivity");
                getActivity().startActivity(i);//第三种
            }


        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(mCotext, mCotext.getString(R.string.cantOpen), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
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
                PackageInfo info = PluginUtils.getPackageInfo(mCotext, plugin.getAbsolutePath());
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
        {
            Toast.makeText(getActivity().getApplicationContext(), "ok,startActivity返回了", Toast.LENGTH_LONG).show();
        }
    }

}
