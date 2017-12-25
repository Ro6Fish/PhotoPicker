package me.rokevin.lib.photopicker.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luokaiwen on 16/6/24.
 */
public class PhotoPickUtil {

    private static final String TAG = PhotoPickUtil.class.getSimpleName();

    public static final int CAMERA = 1; // 拍照
    public static final int PHOTO = 2;  // 从相册中选择
    public static final int CROP = 3;   // 剪切结果

    public String mImageDir;
    public String mImagePath;

    private int mCurType = 0;
    private Activity mActivity;

    private String mFileName;

    private boolean isCrop = true;

    private int mWidth = 600;
    private int mHeight = 600;

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

                    String path = mImagePath;

                    resize(path);

//                    Uri uri = Uri.fromFile(new File(mImagePath));
//
//                    if (mOnPhotoCropListener != null) {
//                        mOnPhotoCropListener.onFinish(uri);
//                    }
                }

                break;

            case PHOTO:// 当选择从本地获取图片时

                if (isCrop) {

                    // 做非空判断，想重新剪裁的时候便不会报异常，下同
                    if (intent != null) {
//                        Log.e(TAG, "onActivityResult: photo intent is not null");

                        String path = UriPathUtil.getPath(mActivity, intent.getData());

//                        Log.e(TAG, "onActivityResult: PHOTO path is " + path);

                        startPhotoCrop(Uri.fromFile(new File(path)));
                        //startPhotoCrop(intent.getData());

                    } else {
//                        Log.e(TAG, "onActivityResult: photo intent is null!!!!!!");
                    }

                } else {

                    // 做非空判断，想重新剪裁的时候便不会报异常，下同
                    if (intent != null) {
//                        Log.e(TAG, "onActivityResult: photo intent is not null");

                        String path = UriPathUtil.getPath(mActivity, intent.getData());

//                        Log.e(TAG, "onActivityResult: PHOTO path is " + path);

//                        if (mOnPhotoCropListener != null) {
//                            mOnPhotoCropListener.onFinish(Uri.fromFile(new File(path)));
//                        }

                        copyFile(path, mImagePath);

                        resize(mImagePath);

                    } else {
//                        Log.e(TAG, "onActivityResult: photo intent is null!!!!!!");
                    }
                }

                break;

            case CROP:// 返回的结果

                Uri data = null;

                if (null == intent || intent.getData() == null) {

//                    Log.e(TAG, "onActivityResult: crop intent is null!!!!!!");

                    if (!TextUtils.isEmpty(mImagePath)) {

                        data = Uri.fromFile(new File(mImagePath));
                    }

                } else {

                    data = intent.getData();
                }

//                Log.e(TAG, "onActivityResult: uri is " + data);
//                Log.e(TAG, "onActivityResult: crop intent is not null");

                if (data != null) {

//                    Log.e(TAG, "onActivityResult: extras is not null");
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
                            photo.compress(Bitmap.CompressFormat.JPEG, 80, iStream);
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

//                    Log.e(TAG, "onActivityResult: extras is null!!!!!!");

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

    /**
     * 设置裁剪后图片的大小
     *
     * @param x
     * @param y
     */
    public void setCropRect(int x, int y) {

        if (x <= 0 || y <= 0) {
            return;
        }

        mWidth = x;
        mHeight = y;
    }

    /**
     * return-data 设置成false是
     *
     * @param uri
     */
    public void startPhotoCrop(Uri uri) {

        // clearAvatarPath();
        /**
         * 判断是否拍照还是选择图片
         * 拍照:path获取,uri处理
         * 图片:path获取,uri处理
         */

//        Log.e(TAG, "startPhotoCrop: uri:" + uri);
//        Log.e(TAG, "startPhotoCrop: mImagePath:" + mImagePath);
//        Log.e(TAG, "cropFile: cropFile:" + cropFile.getAbsolutePath());

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
//        intent.putExtra("outputX", mWidth);
//        intent.putExtra("outputY", mHeight);

        // 小图片可以
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("return-data", true);

        intent.putExtra("return-data", false); // 通过Intent中的data来传递，当数据过大，即超过1M（经测试，这个数值在不同手机还不一样）时就崩了！！！！
        intent.putExtra("noFaceDetection", true);

//        Log.e(TAG, "startPhotoCrop:mImagePath:" + mImagePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mImagePath)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        mActivity.startActivityForResult(intent, CROP);
    }

    public void resize(String path) {

        // 重设置尺寸

        // 压缩

        Bitmap photo = null;
        try {

            // photo = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(Uri.fromFile(new File(path))));

            photo = resizeBitmap(path, 1080, 1920);

            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
            File fImage = new File(path);

            if (fImage.exists()) {
                fImage.delete();
            }

            FileOutputStream iStream = null;

            try {
                fImage.createNewFile();
                iStream = new FileOutputStream(fImage);
                photo.compress(Bitmap.CompressFormat.JPEG, 40, iStream);
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

        } catch (Exception e) {
            e.printStackTrace();

            if (mOnPhotoCropListener != null) {
                mOnPhotoCropListener.onFinish(null);
            }
        }
    }

    public static Bitmap resizeBitmap(String filePath, int targetWidth, int targetHeight) {

        Bitmap bitMapImage = null;
        // First, get the dimensions of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        double sampleSize = 0;
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
                .abs(options.outWidth - targetWidth);
        if (options.outHeight * options.outWidth * 2 >= 1638) {
            // Load, scaling to smallest power of 2 that'll get it <= desired
            // dimensions
            sampleSize = scaleByHeight ? options.outHeight / targetHeight
                    : options.outWidth / targetWidth;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitMapImage = BitmapFactory.decodeFile(filePath, options);
                break;
            } catch (Exception ex) {
                try {
                    sampleSize = sampleSize * 2;
                } catch (Exception ex1) {
                }
            }
        }
        return bitMapImage;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    private OnPhotoCropListener mOnPhotoCropListener;

    public void setOnPhotoCropListener(OnPhotoCropListener onPhotoCropListener) {
        mOnPhotoCropListener = onPhotoCropListener;
    }

    public interface OnPhotoCropListener {
        void onFinish(Uri uri);
    }
}
