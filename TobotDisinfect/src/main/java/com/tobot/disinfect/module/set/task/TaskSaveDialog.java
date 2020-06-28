package com.tobot.disinfect.module.set.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.tobot.common.base.BaseDialog;
import com.tobot.common.constants.ActionConstants;
import com.tobot.common.util.ToastUtils;
import com.tobot.disinfect.R;

/**
 * @author houdeming
 * @date 2020/3/31
 */
public class TaskSaveDialog extends BaseDialog implements View.OnClickListener {
    private EditText editText;
    private OnTaskListener mOnTaskListener;
    private int mMeetObstacleMode;

    public static TaskSaveDialog newInstance(String contentTips) {
        TaskSaveDialog dialog = new TaskSaveDialog();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_KEY, contentTips);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_task_save;
    }

    @Override
    protected void initView(View view) {
        editText = view.findViewById(R.id.et_name);
        RadioButton rbMeetObstacleAvoid = view.findViewById(R.id.rb_meet_obstacle_avoid);
        rbMeetObstacleAvoid.setChecked(true);
        rbMeetObstacleAvoid.setOnClickListener(this);
        view.findViewById(R.id.rb_meet_obstacle_suspend).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        mMeetObstacleMode = ActionConstants.MEET_OBSTACLE_AVOID;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            editText.setText(bundle.getString(DATA_KEY));
        }
    }

    @Override
    protected double getScreenWidthPercentage() {
        return getResources().getInteger(R.integer.dialog_width_weight) / 10.0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_meet_obstacle_avoid:
                mMeetObstacleMode = ActionConstants.MEET_OBSTACLE_AVOID;
                break;
            case R.id.rb_meet_obstacle_suspend:
                mMeetObstacleMode = ActionConstants.MEET_OBSTACLE_SUSPEND;
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.getInstance(getActivity()).show(R.string.name_empty_tips);
                    return;
                }

                if (mOnTaskListener != null) {
                    mOnTaskListener.onTask(content, mMeetObstacleMode);
                }
                break;
            default:
                break;
        }
    }

    public void setOnTaskListener(OnTaskListener listener) {
        mOnTaskListener = listener;
    }

    public interface OnTaskListener {
        /**
         * 任务
         *
         * @param name
         * @param meetObstacleMode
         */
        void onTask(String name, int meetObstacleMode);
    }
}
