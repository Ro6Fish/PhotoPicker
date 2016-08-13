package me.rokevin.lib.photopicker.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import me.rokevin.lib.photopicker.R;
import me.rokevin.lib.photopicker.util.Util;

/**
 * Created by luokaiwen on 15/5/28.
 * <p/>
 * 图片选择器弹框
 */
public class PhotoPickerDialog extends BaseDialog {

    private OnPhotoPickerListener mListener;

    public PhotoPickerDialog(Context context) {
        super(context);
    }

    public void setListener(OnPhotoPickerListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_photo_picker;
    }

    @Override
    protected void showView(View view) {

        Button btnCamera = (Button) view.findViewById(R.id.btn_camera);
        Button btnGallry = (Button) view.findViewById(R.id.btn_gallery);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    mListener.onCameraPick();
                }
                cancel();
            }
        });

        btnGallry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    mListener.onGalleryPick();
                }
                cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancel();
            }
        });
    }

    @Override
    protected int getAnimStyle() {
        return R.style.AnimPhotoPicker;
    }

//    @Override
//    protected int getGravity() {
//        return Gravity.TOP;
//    }

    @Override
    protected int getWidth() {

        return Util.getScreenWidth(mContext);
    }

    public interface OnPhotoPickerListener {

        void onCameraPick();

        void onGalleryPick();
    }
}
