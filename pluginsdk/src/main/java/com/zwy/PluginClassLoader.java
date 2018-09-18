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

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import dalvik.system.DexClassLoader;

/**
 * 插件ClassLoader
 * 
 * @author zhaoweiying
 * @version [版本号, 2016年10月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PluginClassLoader extends DexClassLoader
{
    /**
     *
     */
    private static final HashMap<String, PluginClassLoader> mPluginClassLoaders = new HashMap<String, PluginClassLoader>();

    protected PluginClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent)
    {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    /**
     * return a available classloader which belongs to different apk
     */
    public static PluginClassLoader getClassLoader(String dexPath, Context context, ClassLoader parentLoader)
    {
        PluginClassLoader dLClassLoader = mPluginClassLoaders.get(dexPath);
        if (dLClassLoader != null)
        {
            return dLClassLoader;
        }

        File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        dLClassLoader = new PluginClassLoader(dexPath, dexOutputPath, null, parentLoader);
        mPluginClassLoaders.put(dexPath, dLClassLoader);

        return dLClassLoader;
    }
}
