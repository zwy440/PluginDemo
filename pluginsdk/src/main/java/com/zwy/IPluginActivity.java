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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

/**
 * 插件Activity周期函数管理接口
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface IPluginActivity
{

    public void init(Activity proxyActivity, String apkPath, PackageInfo packageInfo, ClassLoader classLoader);

    public void setIntent(Intent i);

    public void IonStart();

    public void IonRestart();

    public void IonActivityResult(int requestCode, int resultCode, Intent data);

    public void IonResume();

    public void IonPause();

    public void IonStop();

    public void IonDestroy();

    public void IonCreate(Bundle savedInstanceState);

    public void IonSaveInstanceState(Bundle outState);

    public void IonNewIntent(Intent intent);

    public void IonRestoreInstanceState(Bundle savedInstanceState);

    public boolean IonKeyDown(int keyCode, KeyEvent event);

    public boolean IonTouchEvent(MotionEvent event);

    public boolean IonKeyUp(int keyCode, KeyEvent event);

    public void IonWindowAttributesChanged(LayoutParams params);

    public void IonWindowFocusChanged(boolean hasFocus);

    public void IonBackPressed();
    
    public Context getContext();
}
