package com.example.pluginclient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pluginclient.adapter.PackageAdapter;
import com.example.pluginclient.entities.PackageItem;
import com.example.pluginclient.entities.Person;
import com.example.pluginclient.entities.TestItem;
import com.example.pluginclient.view.LoadingLayout;
import com.zwy.PluginActivity;


/**
 * 插件主界面
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class MainActivity extends PluginActivity
{

    /**
     * sdk广播（动态）测试
     */
    public static final String ACTION_PLUGIN_TEST_BROAD_CAST = "com.zwy.plugin.sdk.ACTION_PLUGIN_TEST_BROAD_CAST";

    /**
     * loading区域
     */
    private LoadingLayout mLoadingLayout = null;

    /**
     * 列表
     */
    private ListView mListView = null;

    /**
     * 适配器
     */
    private PackageAdapter mAdapter = null;

    /**
     * 动态广播接收器
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null && ACTION_PLUGIN_TEST_BROAD_CAST.equals(intent.getAction()))
            {
                ((TextView) findViewById(R.id.tips_tv)).setText("接受到广播啦");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 设置布局文件
        setContentView(R.layout.activity_main);
        // 初始化界面
        initView();
        // 加载数据
        loadDatas();
        // 添加监听器
        addListener();
        // 注册动态广播接收器
        registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_PLUGIN_TEST_BROAD_CAST));
    }

    /**
     * 初始化界面
     */
    private void initView()
    {
        mListView = (ListView) findViewById(R.id.main_list);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);

        mAdapter = new PackageAdapter(this, null);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 添加监听器
     * 
     * @see [类、类#方法、类#成员]
     */
    private void addListener()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // 获取安装包信息
                PackageItem item = mAdapter.getItem(position);
                if (item == null)
                {
                    return;
                }

                int action = position % 4;
                switch (action)
                {
                    case 0:
                        Toast.makeText(getApplicationContext(), item.getName(), Toast.LENGTH_LONG).show();
                        break;
                    case 1: // 跳转到第三方应用
                        Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName(item.getPackageName(), item.getLauncherActivity());
                        intent.setComponent(cn);
                        startActivity(intent);

                        break;
                    case 2:
                        Intent i2 = new Intent(MainActivity.this, SecondActivity.class);
                        Person test = new Person();
                        test.setAge(24);
                        test.setName("wentao");
                        ArrayList<String> books = new ArrayList<String>();
                        books.add("book1");
                        books.add("book2");
                        test.setBooks(books);
                        test.setItem(new TestItem("test"));
                        i2.putExtra("extra_test_parcel", test);
                        // 不支持实现  Serializable 接口的序列化对象，只支持实现 Parcelable 接口的序列化对象
                        // i2.putExtra("extra_test_serializable", new TestItem("test"));
                        mActivity.startActivity(i2);
                        break;
                    case 3: // 调起安装程序
                        Intent i3 = new Intent(Intent.ACTION_VIEW);
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "zwy_plugins"
                                + File.separator+"plugin_sdk_demo-unsigned.apk");
                        if (!file.exists())
                        {
                            break;
                        }
                        i3.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        i3.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", getPackageName());
                        startActivity(i3);
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 加载数据
     * 
     * @see [类、类#方法、类#成员]
     */
    private void loadDatas()
    {
        mLoadingLayout.showLoading(true);
        new ListAppTask().execute();
    }

    /**
     * 获取手机中安装的app列表
     * 
     * @author zhaoweiying
     * @version [版本号, 2016年11月26日]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    public class ListAppTask extends AsyncTask<Void, Void, List<PackageItem>>
    {

        protected List<PackageItem> doInBackground(Void... args)
        {
            // list info
            List<ApplicationInfo> listInfo = getPackageManager().getInstalledApplications(0);
            Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(getPackageManager()));

            List<PackageItem> data = new ArrayList<PackageItem>();

            for (int index = 0; index < listInfo.size(); index++)
            {
                try
                {
                    // 获取应用信息
                    ApplicationInfo content = listInfo.get(index);
                    if ((content.flags != ApplicationInfo.FLAG_SYSTEM) && content.enabled)
                    {
                        if (content.icon != 0)
                        {
                            PackageItem item = new PackageItem();
                            // 应用名称
                            item.setName(getPackageManager().getApplicationLabel(content).toString());
                            // 应用包名
                            item.setPackageName(content.packageName);
                            // 应用图标
                            item.setIcon(getPackageManager().getDrawable(content.packageName, content.icon, content));
                            // 应用首个activity名称

                            PackageInfo pi = getPackageManager().getPackageInfo(item.getPackageName(), 0);
                            Intent resolveIntent = new Intent();
                            resolveIntent.setPackage(pi.packageName);

                            List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent,
                                    PackageManager.MATCH_DEFAULT_ONLY);
                            ResolveInfo ri = apps.iterator().next();
                            if (ri != null)
                            {
                                item.setLauncherActivity(ri.activityInfo.name);
                            }
                            data.add(item);
                        }
                    }
                }
                catch (Exception e)
                {
                }
            }

            return data;
        }

        protected void onPostExecute(List<PackageItem> result)
        {
            if (mLoadingLayout != null)
            {
                mLoadingLayout.showLoading(false);
            }
            mAdapter.setDatas(result);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x1001)
        {
            Toast.makeText(getApplicationContext(), "onActivityResult -> " + data.getStringExtra("test"),
                    Toast.LENGTH_LONG).show();
        }
    }

}
