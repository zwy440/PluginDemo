/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.pluginclient.entities;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PackageItem implements Serializable
{
    
    /**
     *
     */
    private static final long serialVersionUID = 3731611908463441125L;

    /**
     * 注意 transient 关键字
     */
    private transient Drawable icon;

    private String name;

    private String packageName;

    private String launcherActivity;

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Drawable getIcon()
    {
        return icon;
    }

    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    public String getLauncherActivity()
    {
        return launcherActivity;
    }

    public void setLauncherActivity(String launcherActivity)
    {
        this.launcherActivity = launcherActivity;
    }

    @Override
    public String toString()
    {
        return "PackageItem [name=" + name + ", packageName=" + packageName + ", launcherActivity=" + launcherActivity
                + "]";
    }
    
}
