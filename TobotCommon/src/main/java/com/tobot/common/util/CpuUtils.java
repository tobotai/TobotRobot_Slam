package com.tobot.common.util;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author houdeming
 * @date 2018/7/6
 */
public class CpuUtils {
    /**
     * 读取CPU信息
     *
     * @return
     */
    public static String getCPUSerial() {
        String cpuAddress = "";
        try {
            // 读取CPU信息
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(inputStreamReader);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                String str = input.readLine();
                if (!TextUtils.isEmpty(str)) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        String strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                }
            }
            inputStreamReader.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuAddress;
    }
}
