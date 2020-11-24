/*
 * Copyright (C) 2020, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 */

package com.gocypher.cybench.utils;

import com.gocypher.cybench.core.utils.JSONUtils;
import com.gocypher.cybench.runConfiguration.BenchmarkState;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.application.PluginPathManager;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class Utils {


    static ResourceBundle titles = ResourceBundle.getBundle("titles");

    public static String convertNumToStringByLength(String value) {
        try {
            return JSONUtils.convertNumToStringByLength(value);
        } catch (NumberFormatException e) {
            return findURI(value);
        }
    }

    private static String findURI(String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return "<HTML><a href=\"" + value + "\">Open " + value + "</a></HTML>";
        }

        return value;
    }


    public static String getKeyName(String key) {
        if (titles.containsKey(key)) {
            return titles.getString(key);
        } else return key;
    }

    public static File[] getPluginClasspathHackyWay() {
        try {


            PluginClassLoader pluginClassLoader = (PluginClassLoader) Class.forName("com.gocypher.cybench.runConfiguration.BenchmarkState").getClassLoader();
            Field myLibDirectories = PluginClassLoader.class.getDeclaredField("myLibDirectories");
            myLibDirectories.setAccessible(true);
            List<String> libDirs = (List<String>) myLibDirectories.get(pluginClassLoader);
            List<File> result = new ArrayList<>();
            for (String libDir : libDirs) {
                File file = new File(libDir);
                if (file.exists()) {
                    File[] files = file.listFiles(BenchmarkState.PLUGINS_JAR_FILTER);
                    List<File> c = Arrays.asList(files);
                    result.addAll(c);
                }
            }
            return result.toArray(new File[result.size()]);

        } catch (Exception e) {
            return null;
        }
    }

    public static File[] getJMHLibFiles() {
        File[] pluginJars;
        File pluginHome = PluginPathManager.getPluginHome("Cybench-Intellij");
        File lib = new File(pluginHome, "lib");
        if (lib.exists()) {
            pluginJars = lib.listFiles(BenchmarkState.PLUGINS_JAR_FILTER);
        } else {
            pluginJars = getPluginClasspathHackyWay();
        }
        return pluginJars;
    }
}
