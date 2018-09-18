package com.example.pluginclient.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pluginclient.R;
import com.example.pluginclient.entities.PackageItem;


/**
 * 适配器
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年11月26日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PackageAdapter extends BaseAdapter
{

    /**
     * 数据
     */
    private List<PackageItem> mDatas = null;

    /**
     * 上下文
     */
    private Context mContext = null;

    /**
     * section
     */
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> mSectionMap = new HashMap<Integer, String>();

    public PackageAdapter(Context context, List<PackageItem> data)
    {
        this.mContext = context;
        this.mDatas = data;
        buildSection();
    }

    /**
     * @param datas
     * @see [类、类#方法、类#成员]
     */
    public void setDatas(List<PackageItem> datas)
    {
        this.mDatas = datas;
        buildSection();
    }

    /**
     * 构建section
     * 
     * @see [类、类#方法、类#成员]
     */
    private void buildSection()
    {
        mSectionMap.clear();
        if (mDatas == null || mDatas.size() == 0)
        {
            return;
        }

        for (int i = 0; i < mDatas.size(); i++)
        {
            PackageItem item = mDatas.get(i);
            if (item != null && !TextUtils.isEmpty(item.getName()))
            {
                String name = item.getName().substring(0, 1).toUpperCase(Locale.getDefault());
                if (!mSectionMap.containsValue(name))
                {
                    mSectionMap.put(i, name);
                }
            }
        }
    }

    @Override
    public int getCount()
    {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public PackageItem getItem(int position)
    {
        if (mDatas == null || position < 0 || position >= mDatas.size())
        {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final PackageItem item = getItem(position);
        ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(R.layout.package_row, parent, false);
            holder = new ViewHolder();
            holder.indexTv = (TextView) convertView.findViewById(R.id.index_tv);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.example_row_iv_image);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.example_row_tv_title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.example_row_tv_description);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivImage.setImageDrawable(item.getIcon());
        holder.tvTitle.setText(item.getName());
        holder.tvDescription.setText(item.getPackageName());

        if (mSectionMap.containsKey(position))
        {
            holder.indexTv.setVisibility(View.VISIBLE);
            holder.indexTv.setText(mSectionMap.get(position));
        }
        else
        {
            holder.indexTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder
    {
        TextView indexTv;

        ImageView ivImage;

        TextView tvTitle;

        TextView tvDescription;
    }

}
