package me.rokevin.photopicker.component.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by luokaiwen on 16/8/11.
 * <p/>
 * SD卡工具类
 */
public class SDUtil {

    private static final String TAG = SDUtil.class.getSimpleName();

    /**
     * 初始化项目中用到的文件夹
     */
    public static void initFileDir() {

        try {
            File file = new File(getSDPath() + "/AAA/");

            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                Log.e(TAG, "mkdirs:" + mkdirs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 判断是否挂载SDcard

        return path;
    }

    public static boolean isMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static void deleteFile(String path) {

        if (!TextUtils.isEmpty(path)) {
            try {
                File file = new File(path);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i].getAbsolutePath());
                    }
                }

                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                }

//                } else {// 目录
//                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
//                        file.delete();
//                    }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clear(String path) {
        deleteFile(path);
    }
}
