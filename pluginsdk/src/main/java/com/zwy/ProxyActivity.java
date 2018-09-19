/*
 * Copyright (C) 2014 likebamboo(李文涛) <likebamboo@163.com>
 *
 * collaborator:田啸,宋思宇
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zwy;

import java.lang.reflect.Constructor;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

import com.zwy.utils.PluginConstants;


/**
 * 代理FragmentActivity
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ProxyActivity extends FragmentActivity
{

    private static final String TAG = ProxyActivity.class.getSimpleName();
    
    /**
     * apk路径
     */
    private static String sApkPath = "";

    /**
     * 需要启动的插件的Activity名称
     */
    private String mClassName = null;

    /**
     * apk插件路径
     */
    private String mApkFilePath = null;

    /**
     * 插件Activity
     */
    private IPluginActivity mPluginActivity = null;

    /**
     * 插件Apk的包信息
     */
    private PackageInfo mPackageInfo = null;

    /**
     * 插件Activity信息
     */
    private ActivityInfo mActivityInfo = null;

    /**
     * 寻找并初始化插件中的Activity
     */
    private void searchActivity()
    {
        mPackageInfo = getPackageManager().getPackageArchiveInfo(mApkFilePath, 1);
        if ((mPackageInfo.activities != null) && (mPackageInfo.activities.length > 0))
        {
            if (TextUtils.isEmpty(mClassName))
            {
                mClassName = mPackageInfo.activities[0].name;
            }
            for (ActivityInfo a : mPackageInfo.activities)
            {
                if (a.name.equals(mClassName))
                {
                    mActivityInfo = a;
                    break;
                }
            }
        }
    }

    /**
     * 获取插件Activity的基本信息
     * 
     * @see [类、类#方法、类#成员]
     */
    private void handleActivityInfo()
    {
        if (mActivityInfo == null)
        {
            return;
        }
        Log.d(TAG, "handleActivityInfo, theme=" + mActivityInfo.theme);
        if (mActivityInfo.theme > 0)
        {
            setTheme(mActivityInfo.theme);
        }
        // TODO: handle mActivityInfo.launchMode here.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            // 获取apk与路径信息
            getApkAndClassPath(getIntent());
        }
        catch (Exception e)
        {
            Log.d(TAG, " start plugin Activity from plugin , should change the intent's classLoader ");
            if (!TextUtils.isEmpty(sApkPath))
            {
                try
                {
                    Intent i = new Intent(getIntent());
                    // 创建插件类的ClassLoader
                    ClassLoader clsLoader = PluginClassLoader.getClassLoader(sApkPath, this, getClassLoader());
                    if (clsLoader != null)
                    {
                        i.setExtrasClassLoader(clsLoader);
                        Log.d(TAG, "changed classLoader to--- >" + clsLoader);
                    }
                    // 重新获取apk与class的路径信息
                    getApkAndClassPath(i);
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        Log.d(TAG, "mClass=" + mClassName + " mApkPath=" + mApkFilePath);
        if (TextUtils.isEmpty(mApkFilePath))
        {
            finish();
            return;
        }

        // 先在插件中找到相应的Activity
        searchActivity();
        // 获取Activity信息
        handleActivityInfo();
        // 初始化目标Activity
        initTargetActivity(mClassName);
        // 启动插件Activity
        if (mPluginActivity != null)
        {
            mPluginActivity.IonCreate(new Bundle());
        }
    }

    /** 
     * 获取apk与class的路径信息
     * @see [类、类#方法、类#成员]
     */
    private void getApkAndClassPath(Intent i)
    {
        if (i == null)
        {
            return;
        }
        mApkFilePath = i.getStringExtra(PluginConstants.PLUGIN_APK_PATH);
        mClassName = i.getStringExtra(PluginConstants.PLUGIN_CLASS);
        sApkPath = mApkFilePath;
    }

    /**
     * 初始化目标Activity
     * 
     * @param className
     */
    protected void initTargetActivity(final String className)
    {
        if (TextUtils.isEmpty(className))
        {
            return;
        }
        Log.d(TAG, "start launch target Activity, className=" + className);
        try
        {
            // 创建插件类的ClassLoader
            ClassLoader classLoader = PluginClassLoader.getClassLoader(mApkFilePath, this, getClassLoader());
            Class<?> localClass = classLoader.loadClass(className);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
            Object instance = localConstructor.newInstance(new Object[] {});
            Log.d(TAG, "instance = " + instance);
            mPluginActivity = (IPluginActivity) instance;
            // 初始化插件Activity
            mPluginActivity.init(this, mApkFilePath, mPackageInfo, classLoader);
            // 设置插件Activity的Intent
            Intent i = new Intent(getIntent());
            i.setExtrasClassLoader(classLoader);
            mPluginActivity.setIntent(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonStart();
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonRestart();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonResume();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonPause();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonStop();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mPluginActivity != null)
        {
            mPluginActivity.IonDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonNewIntent(intent);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPluginActivity != null)
        {
            mPluginActivity.IonBackPressed();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean consume = false;
        if (mPluginActivity != null)
        {
            consume = mPluginActivity.IonKeyDown(keyCode, event);
        }
        if (!consume)
        {
            consume = super.onKeyDown(keyCode, event);
        }
        return consume;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mPluginActivity != null)
        {
            return mPluginActivity.IonTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowAttributesChanged(LayoutParams params)
    {
        super.onWindowAttributesChanged(params);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonWindowAttributesChanged(params);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (mPluginActivity != null)
        {
            mPluginActivity.IonWindowFocusChanged(hasFocus);
        }
    }

    /**
     * 获取Context,该方法仅仅提供给Fragment
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    Context getContext()
    {
        if (mPluginActivity != null)
        {
            return mPluginActivity.getContext();
        }
        return null;
    }

    /**
     * Called by Fragment.startActivityForResult() to implement its behavior.
     */
    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode)
    {
        if (requestCode == -1)
        {
            startActivityForResult(intent, -1);
            return;
        }
        if ((requestCode & 0xffff0000) != 0)
        {
            throw new IllegalArgumentException("pluginSDK-> Can only use lower 16 bits for requestCode");
        }
        Log.d(TAG, "start activity from fragment = " + fragment.toString());
        
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        if (intent == null)
        {
            return;
        }

        if (intent.getComponent() == null)
        {
            Log.d(TAG, "Intent's component info is null，maybe this is a third party request. ");
            super.startActivityForResult(intent, requestCode);
            return;
        }
        changeIntentPackageToProxy(intent);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0)
        {
            for (ResolveInfo resolveInfo : list)
            {
                Log.d(TAG, "start Activity by proxy =>" + resolveInfo.activityInfo.name);
            }
            // 跳转到宿主app中的某个activity
            super.startActivityForResult(intent, requestCode);
            return;
        }
        // 获取apk路径和启动的Activity名称
        String[] names = getPackageAndClassNameFromIntent(intent);
        // 如果apk路径以及activity名称不为空，启动插件中的Activity
        if (!TextUtils.isEmpty(names[0]) && !TextUtils.isEmpty(names[1]))
        {
            startPluginActivityForResult(this, names[0], names[1], intent, requestCode);
        }
    }

    /**
     * 启动插件中的activity
     * 
     * @param activity
     * @param apkPath
     * @param launchActivity
     * @param startIntent
     * @param requestCode
     */
    private void startPluginActivityForResult(Activity activity, String apkPath, String launchActivity,
            Intent startIntent, int requestCode)
    {
        Intent intent = new Intent(startIntent);
        intent.setClass(activity, getClass());
        intent.putExtra(PluginConstants.PLUGIN_APK_PATH, apkPath);
        intent.putExtra(PluginConstants.PLUGIN_CLASS, launchActivity);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public ComponentName startService(Intent service)
    {
        if (service == null)
        {
            return null;
        }
        changeIntentPackageToProxy(service);
        List<ResolveInfo> list = getPackageManager().queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0)
        {
            // 跳转启动宿主的Service
            return super.startService(service);
        }
        // 获取apk路径和启动的Service名称
        String[] names = getPackageAndClassNameFromIntent(service);
        // 如果apk路径以及activity名称不为空，启动插件中的Service
        if (!TextUtils.isEmpty(names[0]) && !TextUtils.isEmpty(names[1]))
        {
            service.setClass(this, ProxyService.class);
            service.putExtra(PluginConstants.PLUGIN_APK_PATH, names[0]);
            service.putExtra(PluginConstants.PLUGIN_CLASS, names[1]);
            return startService(service);
        }
        return null;
    }

    @Override
    public boolean stopService(Intent name)
    {
        if (name == null)
        {
            return false;
        }
        changeIntentPackageToProxy(name);
        List<ResolveInfo> list = getPackageManager().queryIntentServices(name, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0)
        {
            // 跳转启动宿主的Service
            return super.stopService(name);
        }
        name.setClass(this, ProxyService.class);
        return stopService(name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags)
    {
        if (service == null)
        {
            return false;
        }
        changeIntentPackageToProxy(service);
        List<ResolveInfo> list = getPackageManager().queryIntentServices(service, PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0)
        {
            // 跳转启动宿主的Service
            return super.bindService(service, conn, flags);
        }
        // 获取apk路径和启动的Service名称
        String[] names = getPackageAndClassNameFromIntent(service);
        // 如果apk路径以及activity名称不为空，启动插件中的Service
        if (!TextUtils.isEmpty(names[0]) && !TextUtils.isEmpty(names[1]))
        {
            service.setClass(this, ProxyService.class);
            service.putExtra(PluginConstants.PLUGIN_APK_PATH, names[0]);
            service.putExtra(PluginConstants.PLUGIN_CLASS, names[1]);
            return bindService(service, conn, flags);
        }
        return false;
    }

    /**
     * 从Intent中获取请求的apk文件和类名
     * 
     * @param i
     * @return
     * @see [类、类#方法、类#成员]
     */
    private String[] getPackageAndClassNameFromIntent(Intent i)
    {
        String className = "", apkPath = "";
        if (i != null)
        {
            ComponentName componentName = i.getComponent();
            if (componentName != null)
            {
                className = componentName.getClassName();
            }
            apkPath = i.getStringExtra(PluginConstants.PLUGIN_APK_PATH);
            if (TextUtils.isEmpty(apkPath) || "null".equals(apkPath))
            {
                apkPath = mApkFilePath;
            }
        }
        return new String[] {apkPath, className};
    }

    /**
     * 将Intent的packageName转为代理的package
     * 
     * @param i
     * @see [类、类#方法、类#成员]
     */
    private void changeIntentPackageToProxy(Intent i)
    {
        if (i == null)
        {
            return;
        }
        String className = "", packageName = "";
        ComponentName componentName = i.getComponent();
        if (componentName != null)
        {
            className = componentName.getClassName();
            packageName = componentName.getPackageName();
        }
        // 如果获取的packageName 是插件的packageName，将其改为宿主的packageName
        if (mPackageInfo != null && mPackageInfo.packageName.equals(packageName)) {
            packageName = getPackageName();
        }
        i.setClassName(packageName, className);
    }
}
