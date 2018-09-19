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
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;

import com.zwy.utils.PluginConstants;
import com.zwy.utils.PluginUtils;


/**
 * 代理Service
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ProxyService extends Service
{
    /**
     * apk路径
     */
    private String mApkFilePath = "";

    /**
     * 启动的service类名
     */
    private String mLaunchService = "";

    /**
     *
     */
    private IPluginService mPluginService = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int res = super.onStartCommand(intent, flags, startId);
        boolean createResult = createServiceIfNeccessary(intent);
        // 如果创建Service成功
        if (createResult && mPluginService != null)
        {
            res = mPluginService.IonStartCommand(intent, flags, startId);
        }
        return res;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        boolean res = super.onUnbind(intent);
        if (mPluginService != null)
        {
            res = mPluginService.IonUnbind(intent);
        }
        return res;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mPluginService != null)
        {
            mPluginService.IonDestroy();
            mPluginService = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        IBinder res = null;
        boolean createResult = createServiceIfNeccessary(intent);
        // 如果创建Service成功
        if (createResult && mPluginService != null)
        {
            res = mPluginService.IonBind(intent);
        }
        return res;
    }

    /**
     * 创建Service，如果之前Service没有创建
     * 
     * @param intent
     * @return Service是否创建成功
     * @see [类、类#方法、类#成员]
     */
    protected boolean createServiceIfNeccessary(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }
        String launchService = intent.getStringExtra(PluginConstants.PLUGIN_CLASS);
        String pluginApkFilePath = intent.getStringExtra(PluginConstants.PLUGIN_APK_PATH);

        // 如果插件Service的实例存在
        if (mPluginService != null)
        {
            // 且确实是指定的Service
            if (mLaunchService.equals(launchService) && mApkFilePath.equals(pluginApkFilePath))
            {
                // service之前就创建成功
                return true;
            }
        }
        mApkFilePath = pluginApkFilePath;
        mLaunchService = launchService;
        ClassLoader classLoader = PluginClassLoader.getClassLoader(mApkFilePath, this, getClassLoader());
        if (classLoader != null)
        {
            intent.setExtrasClassLoader(classLoader);
        }
        String errInfo = "";
        try
        {
            // 初始化Service
            errInfo = initService();
            if (errInfo == null)
            {
                // 创建Service
                mPluginService.IonCreate();
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 初始化Service
     * 
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    private String initService() throws Exception
    {
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = PluginUtils.getPackageInfo(this, mApkFilePath);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        if (packageInfo == null)
        {
            return "Get Package Info Failed!";
        }
        ClassLoader classLoader = PluginClassLoader.getClassLoader(mApkFilePath, this, getClassLoader());
        Class<?> pluginServiceClass = classLoader.loadClass(mLaunchService);
        try
        {
            mPluginService = (IPluginService) pluginServiceClass.newInstance();
        }
        catch (Throwable e)
        {
            return "new PluginService failed!";
        }
        mPluginService.init(this, mApkFilePath, classLoader, packageInfo);
        return null;
    }
}
