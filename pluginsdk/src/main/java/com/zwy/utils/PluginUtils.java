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
package com.zwy.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * 插件工具类
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PluginUtils
{
    /**
     * 根据apk路径获取APP package 信息
     * 
     * @param context
     * @param apkFilepath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static PackageInfo getPackageInfo(Context context, String apkFilepath)
    {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try
        {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return pkgInfo;
    }

    /**
     * 获取APP图标
     * 
     * @param context
     * @param apkFilepath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static Drawable getAppIcon(Context context, String apkFilepath)
    {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null)
        {
            return null;
        }

        // Workaround for
        // http://code.google.com/p/android/issues/detail?id=9151
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8)
        {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }

        return pm.getApplicationIcon(appInfo);
    }

    /**
     * 获取app label(名称)
     * 
     * @param context
     * @param apkFilepath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static CharSequence getAppLabel(Context context, String apkFilepath)
    {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null)
        {
            return null;
        }

        // Workaround for
        // http://code.google.com/p/android/issues/detail?id=9151
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8)
        {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }

        return pm.getApplicationLabel(appInfo);
    }

    /**
     * 获取插件apk的启动Activity
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getLauncherActivity(Context context, String apkFilepath)
    {
        PackageInfo pi = getPackageInfo(context, apkFilepath);
        if (pi == null)
        {
            return null;
        }
        if (pi.activities == null || pi.activities.length == 0)
        {
            return null;
        }
        return pi.activities[0].name;
    }

}
