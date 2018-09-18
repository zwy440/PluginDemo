/*
 * Copyright (C) 2014 likebamboo(李文涛) <likebamboo@163.com>
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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.IBinder;

/**
 * 插件Service
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PluginService extends Service implements IPluginService
{
    /**
     * apk路径
     */
    protected String mApkFilePath = "";

    /**
     *
     */
    private PluginContext mResContext = null;

    /**
     * dex文件加载
     */
    protected ClassLoader mDexClassLoader = null;

    /**
     * 是否一插件的形式运行
     */
    protected boolean mIsRunInPlugin = false;

    /**
     * 外部service
     */
    protected Service mService = null;

    /**
     * 包信息
     */
    protected PackageInfo mPackageInfo = null;

    @Override
    public void init(Service service, String apkPath, ClassLoader classLoader, PackageInfo packageInfo)
    {
        mService = service;
        mApkFilePath = apkPath;
        mDexClassLoader = classLoader;
        mPackageInfo = packageInfo;
        if (mResContext == null)
        {
            try
            {
                mResContext = new PluginContext(service, 0, apkPath, classLoader);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        attachBaseContext(mResContext);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    @Override
    public Object getSystemService(String name)
    {
        if ((Service.WINDOW_SERVICE.equals(name)) || (Service.SEARCH_SERVICE.equals(name)))
        {
            if (mIsRunInPlugin)
            {
                return mService.getSystemService(name);
            }
            return super.getSystemService(name);
        }
        if (mResContext != null)
        {
            return mResContext.getSystemService(name);
        }
        return super.getSystemService(name);
    }

    @Override
    public String getPackageName()
    {
        if (mIsRunInPlugin)
        {
            return mPackageInfo.packageName;
        }
        return super.getPackageName();
    }

    public PackageInfo getPackageInfo()
    {
        if (mIsRunInPlugin)
        {
            return mPackageInfo;
        }
        return null;
    }

    @Override
    public Context getApplicationContext()
    {
        if (mIsRunInPlugin)
        {
            return mService.getApplicationContext();
        }
        return super.getApplicationContext();
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        if (mIsRunInPlugin)
        {
            return mService.registerReceiver(receiver, filter);
        }
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver)
    {
        if (mIsRunInPlugin)
        {
            mService.unregisterReceiver(receiver);
            return;
        }
        super.unregisterReceiver(receiver);
    }

    @Override
    public IBinder IonBind(Intent intent)
    {
        return onBind(intent);
    }

    @Override
    public void IonCreate()
    {
        onCreate();
    }

    @Override
    public void IonDestroy()
    {
        onDestroy();
    }

    @Override
    public void IonStart(Intent intent, int startId)
    {
        onStart(intent, startId);
    }

    @Override
    public int IonStartCommand(Intent intent, int flags, int startId)
    {
        return onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean IonUnbind(Intent intent)
    {
        return onUnbind(intent);
    }

}
