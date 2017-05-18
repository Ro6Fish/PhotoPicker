package me.rokevin.lib.photopicker.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by luokaiwen on 16/6/24.
 */
public class PhotoPickUtil {

    private static final String TAG = PhotoPickUtil.class.getSimpleName();

    public static final int CAMERA = 1; // 拍照
    public static final int PHOTO = 2;  // 从相册中选择
    public static final int CROP = 3;   // 结果

    public String mImageDir;
    public String mImagePath;

    private int mCurType = 0;
    private Activity mActivity;

    private String mFileName;

    private boolean isCrop = true;

    public PhotoPickUtil(Activity activity) {

        mActivity = activity;
        setImageDir(SDUtil.getSDPath() + "/AAA/");
    }

    public void disableCrop() {

        isCrop = false;
    }

    public void enableCrop() {

        isCrop = true;
    }

    public boolean isCrop() {
        return isCrop;
    }

    /**
     * 设置图片存储的目录
     *
     * @param dir 图片存储目录
     */
    public void setImageDir(String dir) {

        if (!dir.endsWith("/")) {
            dir += "/";
        }

        mImageDir = dir;
    }

    /**
     * 拍照获取图片必须设置个文件路径,裁剪图片时Uri做版本处理
     * 从图库中获取图片做版本处理
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param intent      intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {

            case CAMERA:// 当选择拍照时调用

                if (isCrop) {

                    startPhotoCrop(Uri.fromFile(new File(mImagePath)));

                } else {

                    Uri uri = Uri.fromFile(new File(mImagePath));

                    if (mOnPhotoCropListener != null) {
                        mOnPhotoCropListener.onFinish(uri);
                    }
                }

                break;

            case PHOTO:// 当选择从本地获取图片时

                if (isCrop) {

                    // 做非空判断，想重新剪裁的时候便不会报异常，下同
                    if (intent != null) {
                        Log.e(TAG, "onActivityResult: photo intent is not null");

                        String path = UriPathUtil.getPath(mActivity, intent.getData());

                        Log.e(TAG, "onActivityResult: PHOTO path is " + path);

                        startPhotoCrop(Uri.fromFile(new File(path)));
                        //startPhotoCrop(intent.getData());

                    } else {
                        Log.e(TAG, "onActivityResult: photo intent is null!!!!!!");
                    }

                } else {

                    // 做非空判断，想重新剪裁的时候便不会报异常，下同
                    if (intent != null) {
                        Log.e(TAG, "onActivityResult: photo intent is not null");

                        String path = UriPathUtil.getPath(mActivity, intent.getData());

                        Log.e(TAG, "onActivityResult: PHOTO path is " + path);

                        if (mOnPhotoCropListener != null) {
                            mOnPhotoCropListener.onFinish(Uri.fromFile(new File(path)));
                        }

                    } else {
                        Log.e(TAG, "onActivityResult: photo intent is null!!!!!!");
                    }
                }

                break;

            case CROP:// 返回的结果

                if (null == intent) {
                    Log.e(TAG, "onActivityResult: crop intent is null!!!!!!");
                    return;
                }

                Uri data = intent.getData();

                Log.e(TAG, "onActivityResult: uri is " + data);
                Log.e(TAG, "onActivityResult: crop intent is not null");

                Bundle extras = intent.getExtras();

                if (extras != null) {

                    Log.e(TAG, "onActivityResult: extras is not null");
                    // Bitmap photo = extras.getParcelable("data");
                    Bitmap photo = null;
                    try {
                        photo = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(Uri.fromFile(new File(mImagePath))));

                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        //photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                        File fImage = new File(mImagePath);

                        if (fImage.exists()) {
                            fImage.delete();
                        }

                        FileOutputStream iStream = null;

                        try {
                            fImage.createNewFile();
                            iStream = new FileOutputStream(fImage);
                            photo.compress(Bitmap.CompressFormat.PNG, 100, iStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {

                            try {
                                if (null != iStream) {
                                    iStream.close();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Uri uri = Uri.fromFile(fImage);

                        if (mOnPhotoCropListener != null) {
                            mOnPhotoCropListener.onFinish(uri);
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onActivityResult: extras is null!!!!!!");

                    if (mCurType == CAMERA) {

                        if (mOnPhotoCropListener != null) {
                            mOnPhotoCropListener.onFinish(data);
                        }

                    } else if (mCurType == PHOTO) {

                        if (mOnPhotoCropListener != null) {
                            mOnPhotoCropListener.onFinish(data);
                        }
                    }
                }

                break;
        }
    }

    /**
     * 获取随机名称
     */
    public void getRandomFileName() {

        mFileName = System.currentTimeMillis() + ".jpg";
        mImagePath = mImageDir + mFileName;
    }

    public void callCamera() {

        mCurType = CAMERA;

        getRandomFileName();

        // 指定调用相机拍照后照片的储存路径
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath)));
        mActivity.startActivityForResult(intent, CAMERA);
    }

    public void callPhoto() {

        mCurType = PHOTO;

        getRandomFileName();

        // 获取图片存在的位置
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, PHOTO);
    }

    private File cropFile = new File(Environment.getExternalStorageDirectory(), "faceImage_temp.jpg");
    private Uri imageCropUri = Uri.fromFile(cropFile);

    public void startPhotoCrop(Uri uri) {

        // clearAvatarPath();
        /**
         * 判断是否拍照还是选择图片
         * 拍照:path获取,uri处理
         * 图片:path获取,uri处理
         */

        Log.e(TAG, "startPhotoCrop: uri:" + uri);
        Log.e(TAG, "startPhotoCrop: mImagePath:" + mImagePath);
        Log.e(TAG, "cropFile: cropFile:" + cropFile.getAbsolutePath());

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 2);
//        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
//        intent.putExtra("outputX", 601);
//        intent.putExtra("outputY", 602);

        // 小图片可以
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);

        intent.putExtra("return-data", false); // 通过Intent中的data来传递，当数据过大，即超过1M（经测试，这个数值在不同手机还不一样）时就崩了！！！！
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        mActivity.startActivityForResult(intent, CROP);
    }

    private OnPhotoCropListener mOnPhotoCropListener;

    public void setOnPhotoCropListener(OnPhotoCropListener onPhotoCropListener) {
        mOnPhotoCropListener = onPhotoCropListener;
    }

    public interface OnPhotoCropListener {
        void onFinish(Uri uri);
    }
}
