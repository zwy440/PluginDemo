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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 插件FragmentActivity基类
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PluginActivity extends FragmentActivity implements IPluginActivity
{

    private static final String TAG = PluginActivity.class.getSimpleName();

    /**
     * 是否finishing
     */
    private boolean mFinished = false;

    /**
     * 资源Context
     */
    private PluginContext mResContext = null;

    /**
     * 代理Activity，如果以独立app方式运行，代理就是自己本身,否则为代理Activity
     */
    protected FragmentActivity mActivity = this;

    /**
     * 是否是以插件的方式运行
     */
    protected boolean mIsRunInPlugin = false;

    /**
     * apk文件的路径
     */
    protected String mApkFilePath = "";

    /**
     * 根View
     */
    protected View mRootView = null;

    /**
     * apk包信息
     */
    protected PackageInfo mPackageInfo = null;

    @Override
    public void init(Activity proxyActivity, String apkPath, PackageInfo packageInfo, ClassLoader classLoader)
    {
        // 是以插件的方式运行
        mIsRunInPlugin = true;
        mApkFilePath = apkPath;
        mPackageInfo = packageInfo;

        if (mResContext == null)
        {
            mResContext = new PluginContext(proxyActivity, 0, apkPath, classLoader);
        }
        attachBaseContext(mResContext);

        mActivity = (FragmentActivity) proxyActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate:run as a plugin?:" + mIsRunInPlugin + ",and proxy is" + mActivity);
        // 如果以插件的方式启动
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onCreate(savedInstanceState);
    }


    /**
     * Called by Fragment.startActivityForResult() to implement its behavior.
     */
    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode)
    {
        if (mIsRunInPlugin)
        {
            mActivity.startActivityFromFragment(fragment, intent, requestCode);
            return;
        }
        super.startActivityFromFragment(fragment, intent, requestCode);
    }
    
    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        if (mIsRunInPlugin)
        {
            mActivity.startActivityForResult(intent, requestCode);
            return;
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setContentView(View view)
    {
        if (mIsRunInPlugin)
        {
            mRootView = view;
            mActivity.setContentView(view);
            return;
        }
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, LayoutParams params)
    {
        if (mIsRunInPlugin)
        {
            mRootView = view;
            mActivity.setContentView(view, params);
            return;
        }
        super.setContentView(view, params);
    }

    @Override
    public void setContentView(int layoutResId)
    {
        if (mIsRunInPlugin)
        {
            mRootView = LayoutInflater.from(mResContext).inflate(layoutResId, null);
            mActivity.setContentView(mRootView);
            return;
        }
        super.setContentView(layoutResId);
    }

    @Override
    public void addContentView(View view, LayoutParams params)
    {
        if (mIsRunInPlugin)
        {
            mActivity.addContentView(view, params);
            return;
        }
        super.addContentView(view, params);
    }

    @Override
    public View findViewById(int id)
    {
        if (mIsRunInPlugin && mRootView != null)
        {
            View v = mRootView.findViewById(id);
            if (v == null)
            {
                return super.findViewById(id);
            }
            return v;
        }
        return super.findViewById(id);
    }

    @Override
    public LayoutInflater getLayoutInflater()
    {
        if (mIsRunInPlugin)
        {
            if (mResContext != null)
            {
                return LayoutInflater.from(mResContext);
            }
            return mActivity.getLayoutInflater();
        }
        return super.getLayoutInflater();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode)
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getSharedPreferences(name, mode);
        }
        return super.getSharedPreferences(name, mode);
    }

    @Override
    public Context getApplicationContext()
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getApplicationContext();
        }
        return super.getApplicationContext();
    }

    @Override
    public WindowManager getWindowManager()
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getWindowManager();
        }
        return super.getWindowManager();
    }

    @Override
    public Window getWindow()
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getWindow();
        }
        return super.getWindow();
    }

    @Override
    public Object getSystemService(String name)
    {
        if (WINDOW_SERVICE.equals(name) || SEARCH_SERVICE.equals(name))
        {
            if (mIsRunInPlugin)
            {
                return mActivity.getSystemService(name);
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
    public boolean isFinishing()
    {
        if (mIsRunInPlugin)
        {
            return mFinished;
        }
        return super.isFinishing();
    }

    @Override
    public void finish()
    {
        if (mIsRunInPlugin)
        {
            //
            try
            {
                Field resultCodeField = Activity.class.getDeclaredField("mResultCode");
                resultCodeField.setAccessible(true);
                int iresultCode = ((Integer) resultCodeField.get(this)).intValue();
                Field intentField = Activity.class.getDeclaredField("mResultData");
                intentField.setAccessible(true);
                Intent intent = (Intent) intentField.get(this);
                mActivity.setResult(iresultCode, intent);
                mActivity.finish();
                mFinished = true;
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
            }
        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do somethings
            return;
        }
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mIsRunInPlugin)
        {
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed()
    {
        if (mIsRunInPlugin)
        {
            try
            {
                super.onBackPressed();
            }
            catch (Exception e)
            {
                finish();
            }
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (mIsRunInPlugin)
        {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
    {
        if (mIsRunInPlugin)
        {
            return false;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public Context getContext()
    {
        if (mIsRunInPlugin)
        {
            // TODO maybe should do something
            return mResContext;
        }
        return this;
    }

    /**
     * 获取插件包信息
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public PackageInfo getPackageInfo()
    {
        if (mIsRunInPlugin)
        {
            return mPackageInfo;
        }
        return null;
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

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
    {
        if (mIsRunInPlugin)
        {
            return mActivity.registerReceiver(receiver, filter);
        }
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver)
    {
        if (mIsRunInPlugin)
        {
            mActivity.unregisterReceiver(receiver);
            return;
        }
        super.unregisterReceiver(receiver);
    }

    @Override
    public ComponentName startService(Intent intent)
    {
        if (mIsRunInPlugin)
        {
            return mActivity.startService(intent);
        }
        return super.startService(intent);
    }

    @Override
    public boolean stopService(Intent name)
    {
        if (mIsRunInPlugin)
        {
            return mActivity.stopService(name);
        }
        return super.stopService(name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags)
    {
        if (mIsRunInPlugin)
        {
            return mActivity.bindService(service, conn, flags);
        }
        return super.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn)
    {
        if (mIsRunInPlugin)
        {
            mActivity.unbindService(conn);
            return;
        }
        super.unbindService(conn);
    }

    // ------------------------------------------------------------------------
    // methods override from FragmentActivity
    // ------------------------------------------------------------------------

    @Override
    public FragmentManager getSupportFragmentManager()
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getSupportFragmentManager();
        }
        return super.getSupportFragmentManager();
    }

    @Override
    public LoaderManager getSupportLoaderManager()
    {
        if (mIsRunInPlugin)
        {
            return mActivity.getSupportLoaderManager();
        }
        return super.getSupportLoaderManager();
    }

    @Override
    public void IonStart()
    {
        onStart();
    }

    @Override
    public void IonRestart()
    {
        onRestart();
    }

    @Override
    public void IonActivityResult(int requestCode, int resultCode, Intent data)
    {
        onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void IonResume()
    {
        onResume();
    }

    @Override
    public void IonPause()
    {
        onPause();
    }

    @Override
    public void IonStop()
    {
        onStop();
    }

    @Override
    public void IonDestroy()
    {
        onDestroy();
    }

    @Override
    public void IonCreate(Bundle savedInstanceState)
    {
        onCreate(savedInstanceState);
    }

    @Override
    public void IonSaveInstanceState(Bundle outState)
    {
        onSaveInstanceState(outState);
    }

    @Override
    public void IonNewIntent(Intent intent)
    {
        onNewIntent(intent);
    }

    @Override
    public void IonRestoreInstanceState(Bundle savedInstanceState)
    {
        onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean IonKeyDown(int keyCode, KeyEvent event)
    {
        return onKeyDown(keyCode, event);
    }

    @Override
    public boolean IonTouchEvent(MotionEvent event)
    {
        return onTouchEvent(event);
    }

    @Override
    public boolean IonKeyUp(int keyCode, KeyEvent event)
    {
        return onKeyUp(keyCode, event);
    }

    @Override
    public void IonWindowAttributesChanged(WindowManager.LayoutParams params)
    {
        onWindowAttributesChanged(params);
    }

    @Override
    public void IonWindowFocusChanged(boolean hasFocus)
    {
        onWindowFocusChanged(hasFocus);
    }

    @Override
    public void IonBackPressed()
    {
        onBackPressed();
    }

}
