package com.tobot.common.util;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2018/11/5
 */
public class FileParser {
    private static final String TAG = FileParser.class.getSimpleName();

    /**
     * pull解析
     *
     * @param path
     * @return
     */
    public static List<String> pullParse(String path) {
        FileInputStream inputStream = null;
        List<String> data = null;
        try {
            inputStream = getFileInputStream(path);
            if (inputStream != null) {
                data = new ArrayList<>();
                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                parser.setInput(inputStream, "utf-8");
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String name = parser.getName();
                        if (TextUtils.equals("item", name)) {
                            String text = parser.nextText();
                            Log.i(TAG, "text=" + text);
                            if (!TextUtils.isEmpty(text)) {
                                data.add(text);
                            }
                        }
                    }
                    eventType = parser.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private static FileInputStream getFileInputStream(String path) throws FileNotFoundException {
        FileInputStream inputStream = null;
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                // 默认读第一个
                String fileName = files[0].getName();
                if (fileName.endsWith(".txt")) {
                    String fileSrc = path.concat(File.separator).concat(fileName);
                    inputStream = new FileInputStream(fileSrc);
                }
            }
        }
        return inputStream;
    }
}
