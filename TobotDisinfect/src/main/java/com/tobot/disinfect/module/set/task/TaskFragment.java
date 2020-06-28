package com.tobot.disinfect.module.set.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tobot.common.base.BaseFragment;
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
 * @date 2020/5/29
 */
public class TaskFragment extends BaseFragment implements View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener<TaskBean>, TaskDetailDialog.OnDeleteListener {
    private static final int SPAN_COUNT = 5;
    private TaskAdapter mTaskAdapter;
    private TaskBean mTaskBean;
    private TaskDetailDialog mTaskDetailDialog;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.btn_create_task).setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));
        int spaceSize = getResources().getDimensionPixelSize(R.dimen.recycler_item_space_size);
        recyclerView.addItemDecoration(new GridDecoration(spaceSize, spaceSize, spaceSize, spaceSize));
        mTaskAdapter = new TaskAdapter(getActivity(), R.layout.recycler_item_task);
        mTaskAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mTaskAdapter);
        setData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            setData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isTaskDetailDialogShow()) {
            mTaskDetailDialog.getDialog().dismiss();
            mTaskDetailDialog = null;
        }
    }

    @Override
    public void onItemClick(int position, TaskBean data) {
        mTaskBean = data;
        if (!isTaskDetailDialogShow()) {
            mTaskDetailDialog = TaskDetailDialog.newInstance(data);
            mTaskDetailDialog.setOnDeleteListener(this);
            mTaskDetailDialog.show(getFragmentManager(), "TASK_DETAIL_DIALOG");
        }
    }

    @Override
    public void onDelete() {
        if (mTaskBean != null) {
            // 删除数据
            MyDBSource.getInstance(getActivity()).deleteTask(mTaskBean.getMapName(), mTaskBean.getName());
            MyDBSource.getInstance(getActivity()).deleteTaskDetail(mTaskBean.getMapName(), mTaskBean.getName());
            setData();
            showToastTips(getString(R.string.delete_success_tips));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_create_task) {
            List<LocationBean> locationList = BaseData.getInstance().getOriginalData();
            if (locationList != null && !locationList.isEmpty()) {
                Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
                intent.putParcelableArrayListExtra(BaseConstant.DATA_KEY, (ArrayList<? extends Parcelable>) locationList);
                startActivityForResult(intent, 1);
                return;
            }
            showToastTips(getString(R.string.no_location_tips));
        }
    }

    private void setData() {
        mTaskAdapter.setData(MyDBSource.getInstance(getActivity()).queryTask(BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(getActivity()))));
    }

    private boolean isTaskDetailDialogShow() {
        return mTaskDetailDialog != null && mTaskDetailDialog.getDialog() != null && mTaskDetailDialog.getDialog().isShowing();
    }
}
