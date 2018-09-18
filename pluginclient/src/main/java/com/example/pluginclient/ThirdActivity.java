package com.example.pluginclient;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.zwy.PluginActivity;


public class ThirdActivity extends PluginActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Button bt = new Button(this);
        bt.setText("测试");
        bt.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 250));
        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        setContentView(bt);
    }

    @Override
    public void finish()
    {
        setResult(RESULT_OK);
        super.finish();
    }

}
