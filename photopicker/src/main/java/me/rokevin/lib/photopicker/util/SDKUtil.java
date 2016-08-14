package me.rokevin.lib.photopicker.util;

import android.os.Build;

/**
 * Created by luokaiwen on 16/8/12.
 * SDK相关工具
 */
public class SDKUtil {

    /**
     * 判断sdk版本是否大于等于19
     *
     * @return true 大于等于19 false
     */
    public static boolean isExceedKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
