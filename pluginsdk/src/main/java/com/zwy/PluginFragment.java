package com.zwy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * webView fragment
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class PluginFragment extends Fragment
{

    /**
     * 上下文
     */
    protected Context mCotext = null;

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getActivity() instanceof ProxyActivity)
        {
            mCotext = ((ProxyActivity) getActivity()).getContext();
            inflater = LayoutInflater.from(mCotext);
        }
        else
        {
            mCotext = getActivity();
        }
        return onCreateView(inflater, savedInstanceState);
    }

    public abstract View onCreateView(LayoutInflater inflater, Bundle savedInstanceState);
}
