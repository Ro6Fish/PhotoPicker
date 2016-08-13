package me.rokevin.photopicker.component.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.InjectView;
import butterknife.OnClick;
import me.rokevin.lib.photopicker.util.PhotoPickUtil;
import me.rokevin.lib.photopicker.util.SDUtil;
import me.rokevin.lib.photopicker.widget.dialog.PhotoPickerDialog;
import me.rokevin.photopicker.R;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.sdv_photo)
    SimpleDraweeView sdvPhoto;

    private PhotoPickerDialog mDialog;

    private PhotoPickUtil mPhotoPickUtil = new PhotoPickUtil(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDialog();

        mPhotoPickUtil.setOnPhotoCropListener(new PhotoPickUtil.OnPhotoCropListener() {
            @Override
            public void onFinish(Uri uri) {
                sdvPhoto.setImageURI(uri);
            }
        });

        SDUtil.initFileDir();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoPickUtil.onActivityResult(requestCode, resultCode, data);
    }

    public void initDialog() {

        mDialog = new PhotoPickerDialog(mContext);
        mDialog.setListener(new PhotoPickerDialog.OnPhotoPickerListener() {
            @Override
            public void onCameraPick() {
                mPhotoPickUtil.callCamera();
            }

            @Override
            public void onGalleryPick() {
                mPhotoPickUtil.callPhoto();
            }
        });
    }

    @OnClick(R.id.sdv_photo)
    public void doPick() {

        mDialog.showDialog();
        Log.e(TAG, "haha");
    }

    @OnClick(R.id.btn_clear)
    public void doClear() {

        SDUtil.clear(SDUtil.getSDPath() + "/AAA/");
    }
}
