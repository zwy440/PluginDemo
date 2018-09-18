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

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;

/**
 * 插件的Context
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PluginContext extends ContextThemeWrapper
{
    private static final String TAG = PluginContext.class.getSimpleName();

    /**
     * classLoader
     */
    private ClassLoader mClassLoader = null;

    /**
     * asset
     */
    private AssetManager mAsset = null;

    /**
     * 资源
     */
    private Resources mResources = null;

    /**
     * 主题
     */
    private Resources.Theme mTheme = null;

    /**
     * 宿主的context对象 （用于在插件里面获取宿主的资源）
     */
    private Context mOutContext = null;
    
    /**
     * packageInfo
     */
    private PackageInfo mPackageInfo = null;

    /**
     * @param apkPath
     * @return
     */
    private AssetManager getSelfAssets(String apkPath)
    {
        AssetManager instance = null;
        try
        {
            instance = (AssetManager) AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(instance, apkPath);
            Log.i(TAG, "load res from apk file ---->" + apkPath);
            return instance;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 获取插件自身的资源对象
     * 
     * @param ctx
     * @param selfAsset
     * @return
     */
    private Resources getSelfRes(Context ctx, AssetManager selfAsset)
    {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        Configuration con = ctx.getResources().getConfiguration();
        return new Resources(selfAsset, metrics, con);
    }

    /**
     * 获取插件自身的主题
     * 
     * @param selfResources
     * @return
     * @see [类、类#方法、类#成员]
     */
    private Resources.Theme getSelfTheme(Resources selfResources)
    {
        Resources.Theme theme = selfResources.newTheme();
        theme.setTo(super.getTheme());
        return theme;
    }

    public PluginContext(Context base, int themeres, String apkPath, ClassLoader classLoader)
    {
        this(base, themeres, apkPath, classLoader, null);
    }

    public PluginContext(Context base, int themeres, String apkPath, ClassLoader classLoader, Resources orignalRes)
    {
        super(base, themeres);
        mOutContext = base;
        mClassLoader = classLoader;
        if (orignalRes != null)
        {
            mAsset = orignalRes.getAssets();
            mResources = orignalRes;
        }
        else
        {
            mAsset = getSelfAssets(apkPath);
            mResources = getSelfRes(base, mAsset);
        }
        mTheme = getSelfTheme(mResources);
        // 获取packageInfo
        if (mOutContext.getPackageManager() != null) {
            mPackageInfo = mOutContext.getPackageManager().getPackageArchiveInfo(apkPath, 1);
        }
    }

    @Override
    public Resources getResources()
    {
        return mResources;
    }

    @Override
    public AssetManager getAssets()
    {
        return mAsset;
    }

    @Override
    public Resources.Theme getTheme()
    {
        return mTheme;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        if (mClassLoader != null)
        {
            return mClassLoader;
        }
        return super.getClassLoader();
    }

    /**
     * 用于在插件中获取宿主的资源信息
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Context getOutContext()
    {
        return mOutContext;
    }

    @Override
    public String getPackageName() {
        if (mPackageInfo != null) {
            return mPackageInfo.packageName;
        }
        return super.getPackageName();
    }
    
}
