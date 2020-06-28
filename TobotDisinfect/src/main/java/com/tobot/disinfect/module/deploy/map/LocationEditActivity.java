package com.tobot.disinfect.module.deploy.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slamtec.slamware.robot.Pose;
import com.tobot.common.base.BaseActivity;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.slam.SlamManager;
import com.tobot.slam.data.LocationBean;

/**
 * @author houdeming
 * @date 2020/3/14
 */
public class LocationEditActivity extends BaseActivity implements View.OnClickListener {
    private EditText etNumber, etNameChina, etNameEnglish;
    private String mNumber;
    private boolean isRequestPose;
    private LocationThread mLocationThread;
    private Pose mPose;
    private LocationBean mLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_edit);
        TextView tvHead = findViewById(R.id.tv_header);
        etNumber = findViewById(R.id.et_number);
        etNameChina = findViewById(R.id.et_name_china);
        etNameEnglish = findViewById(R.id.et_name_english);
        tvHead.setText(R.string.tv_title_edit_location);
        findViewById(R.id.btn_update_location).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        LocationBean bean = getIntent().getParcelableExtra(BaseConstant.DATA_KEY);
        mLocationBean = bean;
        if (bean != null) {
            mNumber = bean.getLocationNumber();
            etNumber.setText(mNumber);
            etNameChina.setText(bean.getLocationNameChina());
            etNameEnglish.setText(bean.getLocationNameEnglish());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationThread != null) {
            mLocationThread.interrupt();
            mLocationThread = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_update_location) {
            isRequestPose = true;
            mLocationThread = new LocationThread();
            mLocationThread.start();
            return;
        }

        if (id == R.id.btn_confirm) {
            confirm();
        }
    }

    private void confirm() {
        String number = etNumber.getText().toString().trim();
        String nameChina = etNameChina.getText().toString().trim();
        String nameEnglish = etNameEnglish.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            showToastTips(getString(R.string.number_empty_tips));
            return;
        }
        // 编号格式必须2位数或以上
        if (number.length() < 2) {
            showToastTips(getString(R.string.number_format_error_tips));
            return;
        }
        // 如果修改编号，则不能与存在的编号一样
        if (!TextUtils.equals(number, mNumber)) {
            LocationBean bean = MyDBSource.getInstance(this).queryLocation(number);
            // 如果已经包含该名称的话，就不让更改
            if (bean != null) {
                showToastTips(getString(R.string.number_edit_fail_tips));
                return;
            }
        }
        // 中文名称不为空的情况
        if (!TextUtils.isEmpty(nameChina)) {
            LocationBean bean = MyDBSource.getInstance(this).queryLocationByChineseName(nameChina);
            if (bean != null) {
                // 如果该名称是在要修改的编号中，则不处理
                if (!TextUtils.equals(bean.getLocationNumber(), mNumber)) {
                    showToastTips(getString(R.string.name_china_edit_fail_tips));
                    return;
                }
            }
        }
        // 英文名称不为空的情况
        if (!TextUtils.isEmpty(nameEnglish)) {
            LocationBean bean = MyDBSource.getInstance(this).queryLocationByEnglishName(nameEnglish);
            if (bean != null) {
                // 如果该名称是在要修改的编号中，则不处理
                if (!TextUtils.equals(bean.getLocationNumber(), mNumber)) {
                    showToastTips(getString(R.string.name_english_edit_fail_tips));
                    return;
                }
            }
        }
        // 更新点位置的情况
        if (isRequestPose && mPose == null) {
            isRequestPose = false;
            showToastTips(getString(R.string.pose_request_fail_tips));
            return;
        }

        if (mLocationBean != null) {
            mLocationBean.setLocationNumber(number);
            mLocationBean.setLocationNameChina(nameChina);
            mLocationBean.setLocationNameEnglish(nameEnglish);
            if (mPose != null) {
                mLocationBean.setX(mPose.getX());
                mLocationBean.setY(mPose.getY());
                mLocationBean.setYaw(mPose.getYaw());
            }
            MyDBSource.getInstance(this).updateLocation(mNumber, mLocationBean);
        }

        Intent data = new Intent();
        data.putExtra(BaseConstant.NUMBER_KEY, mNumber);
        data.putExtra(BaseConstant.DATA_KEY, mLocationBean);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private class LocationThread extends Thread {

        @Override
        public void run() {
            super.run();
            mPose = SlamManager.getInstance().getPose();
            showToastTips(getString(mPose != null ? R.string.pose_request_success_tips : R.string.pose_request_fail_tips));
        }
    }
}
