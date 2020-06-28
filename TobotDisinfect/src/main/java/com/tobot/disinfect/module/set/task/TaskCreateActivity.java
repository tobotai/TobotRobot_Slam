package com.tobot.disinfect.module.set.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tobot.common.base.BaseEventActivity;
import com.tobot.common.base.BaseRecyclerAdapter;
import com.tobot.common.view.GridDecoration;
import com.tobot.disinfect.R;
import com.tobot.disinfect.base.BaseConstant;
import com.tobot.disinfect.base.BaseData;
import com.tobot.disinfect.db.MyDBSource;
import com.tobot.disinfect.entity.TaskBean;
import com.tobot.slam.data.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/3/31
 */
public class TaskCreateActivity extends BaseEventActivity implements View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener<LocationBean>, TaskSaveDialog.OnTaskListener {
    private static final int SPAN_COUNT = 5;
    private TextView tvSelectAll;
    private Button btnConfirm;
    private TaskCreateAdapter mAdapter;
    private List<LocationBean> mAllData;
    private int mAllSize;
    private List<LocationBean> mSelectList = new ArrayList<>();
    private TaskSaveDialog mTaskSaveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        TextView tvHead = findViewById(R.id.tv_header);
        tvHead.setText(getString(R.string.btn_create_task));
        tvSelectAll = findViewById(R.id.tv_select_all);
        btnConfirm = findViewById(R.id.btn_confirm);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        tvSelectAll.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        int spaceSize = getResources().getDimensionPixelSize(R.dimen.recycler_item_space_size);
        recyclerView.addItemDecoration(new GridDecoration(spaceSize, spaceSize, spaceSize, spaceSize));
        mAdapter = new TaskCreateAdapter(this, R.layout.recycler_item_task_create);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mAllData = getIntent().getParcelableArrayListExtra(BaseConstant.DATA_KEY);
        mAdapter.setData(mAllData);
        mAllSize = mAllData.size();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeTaskSaveDialog();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_select_all) {
            mSelectList.clear();
            if (tvSelectAll.isSelected()) {
                tvSelectAll.setSelected(false);
            } else {
                tvSelectAll.setSelected(true);
                mSelectList.addAll(mAllData);
            }
            boolean isSelect = tvSelectAll.isSelected();
            setSelect(isSelect);
            btnConfirm.setEnabled(isSelect);
            mAdapter.setSelectData(mSelectList);
            return;
        }

        if (id == R.id.btn_confirm) {
            if (!isTaskSaveDialogShow()) {
                mTaskSaveDialog = TaskSaveDialog.newInstance("");
                mTaskSaveDialog.setOnTaskListener(this);
                mTaskSaveDialog.show(getFragmentManager(), "TASK_DIALOG");
            }
        }
    }

    @Override
    public void onItemClick(int position, LocationBean data) {
        if (mSelectList.contains(data)) {
            mSelectList.remove(data);
            if (mSelectList.isEmpty()) {
                btnConfirm.setEnabled(false);
            }
            // 取消全选
            if (tvSelectAll.isSelected()) {
                setSelect(false);
            }
        } else {
            mSelectList.add(data);
            if (!btnConfirm.isEnabled()) {
                btnConfirm.setEnabled(true);
            }
            // 设置全选
            if (mSelectList.size() == mAllSize) {
                setSelect(true);
            }
        }
        mAdapter.setSelectData(mSelectList);
    }

    @Override
    public void onTask(String name, int meetObstacleMode) {
        String mapName = BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(this));
        TaskBean bean = MyDBSource.getInstance(this).queryTask(mapName, name);
        if (bean != null) {
            showToastTips(getString(R.string.task_name_exist_tips));
            return;
        }

        closeTaskSaveDialog();
        bean = new TaskBean();
        bean.setName(name);
        bean.setMapName(mapName);
        bean.setMode(meetObstacleMode);
        MyDBSource.getInstance(this).insertTask(bean);
        MyDBSource.getInstance(this).insertTaskDetail(name, mSelectList);
        showToastTips(getString(R.string.task_create_success));
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void setSelect(boolean isSelect) {
        tvSelectAll.setSelected(isSelect);
        tvSelectAll.setText(isSelect ? getString(R.string.tv_select_all_cancel) : getString(R.string.tv_select_all));
    }

    private void closeTaskSaveDialog() {
        if (isTaskSaveDialogShow()) {
            mTaskSaveDialog.getDialog().dismiss();
            mTaskSaveDialog = null;
        }
    }

    private boolean isTaskSaveDialogShow() {
        return mTaskSaveDialog != null && mTaskSaveDialog.getDialog() != null && mTaskSaveDialog.getDialog().isShowing();
    }
}
