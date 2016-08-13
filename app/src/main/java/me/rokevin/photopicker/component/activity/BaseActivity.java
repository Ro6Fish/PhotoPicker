package me.rokevin.photopicker.component.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by luokaiwen on 16/8/11.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext = BaseActivity.this;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
    }
}
