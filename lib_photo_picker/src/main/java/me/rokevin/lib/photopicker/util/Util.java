package me.rokevin.lib.photopicker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Util {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dipToPixels(Context context, int dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dip2pxF(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        // Rect frame = new Rect();
        // activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // return frame.top;
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusBarHeight;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取手机IP地址
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpAddress", "获取IP地址失败");
        }
        return null;
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = getPackageInfo(context).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = getPackageInfo(context).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getChannel(Context context) {

        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo("com.pengtx.wonderworld", PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String channel = "ww";

        if (null != appInfo && null != appInfo.metaData.getString("UMENG_CHANNEL")) {

            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        }

        return channel;
    }

    public static PackageInfo getPackageInfo(Context context) throws Exception {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeviceMac(Context context) {
        try {
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            if (!TextUtils.isEmpty(mac)) {
                return mac;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
//        // 取 drawable 的长宽
//        int w = drawable.getIntrinsicWidth();
//        int h = drawable.getIntrinsicHeight();
//
//        // 取 drawable 的颜色格式
//        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                : Bitmap.Config.RGB_565;
//        // 建立对应 bitmap
//        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
//        // 建立对应 bitmap 的画布
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, w, h);
//        // 把 drawable 内容画到画布中
//        drawable.draw(canvas);
//        return bitmap;

        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }
}
