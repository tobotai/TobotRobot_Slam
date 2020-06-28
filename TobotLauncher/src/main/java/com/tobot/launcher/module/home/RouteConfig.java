package com.tobot.launcher.module.home;

import com.tobot.common.util.LogUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author houdeming
 * @date 2019/9/27
 */
public class RouteConfig {
    private static final String TAG = RouteConfig.class.getSimpleName();

    public void config() {
        Executors.newCachedThreadPool().submit(new RouteRunnable());
    }

    private class RouteRunnable implements Runnable {

        @Override
        public void run() {
            String[] cmdArray = new String[]{
                    // 把内网设置成以太网
                    "ip ru add from 192.168.11.0/24 lookup eth0 \n",
                    // 其他的都设置为wlan0
                    "ip ru add from all lookup wlan0 \n",
                    "ip route add 192.168.11.0/24 dev eth0 proto static scope link table wlan0 \n"};

            for (int i = 0, length = cmdArray.length; i < length; i++) {
                String result = execRootCmd(cmdArray[i]);
                LogUtils.i(TAG, "route execute result=" + result);
                if (i != length - 1) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            // 经过Root处理的android系统即有su命令
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = dis.readLine()) != null) {
                builder.append(line);
            }
            result = builder.toString();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, "route execute error=" + e.getMessage());
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
