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

/**
 * 插件Service生命周期管理接口
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract interface IPluginService
{
    public abstract void init(Service service, String apkPath, ClassLoader classLoader, PackageInfo packageInfo);

    public abstract IBinder IonBind(Intent intent);

    public abstract void IonCreate();

    public abstract void IonDestroy();

    public abstract void IonStart(Intent intent, int startId);

    public abstract int IonStartCommand(Intent intent, int flags, int startId);

    public abstract boolean IonUnbind(Intent intent);
}
