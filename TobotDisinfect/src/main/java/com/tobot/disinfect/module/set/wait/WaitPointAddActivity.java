package com.tobot.disinfect.module.set.wait;

import android.app.Activity;
import android.content.Intent;
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
import com.tobot.disinfect.module.set.task.TaskCreateAdapter;
import com.tobot.slam.data.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/30
 */
public class WaitPointAddActivity extends BaseEventActivity implements View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener<LocationBean> {
    private static final int SPAN_COUNT = 5;
    private Button btnConfirm;
    private TaskCreateAdapter mAdapter;
    private List<LocationBean> mSelectList;
    private boolean isOperateData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_point_add);
        TextView tvHead = findViewById(R.id.tv_header);
        tvHead.setText(getString(R.string.btn_add_wait_point));
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        Intent intent = getIntent();
        List<LocationBean> allData = intent.getParcelableArrayListExtra(BaseConstant.DATA_KEY);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        int spaceSize = getResources().getDimensionPixelSize(R.dimen.recycler_item_space_size);
        recyclerView.addItemDecoration(new GridDecoration(spaceSize, spaceSize, spaceSize, spaceSize));
        mAdapter = new TaskCreateAdapter(this, R.layout.recycler_item_task_create);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setSelectDataNoNotify(mSelectList);
        mAdapter.setData(allData);
    }

    @Override
    public void onItemClick(int position, LocationBean data) {
        if (mSelectList == null) {
            mSelectList = new ArrayList<>();
        }
        isOperateData = true;

        if (mSelectList.contains(data)) {
            mSelectList.remove(data);
            if (mSelectList.isEmpty()) {
                btnConfirm.setEnabled(false);
            }
        } else {
            if (mSelectList.size() >= BaseConstant.MAX_WAIT_POINT_COUNT) {
                showToastTips(getString(R.string.wait_point_count_over_tips, BaseConstant.MAX_WAIT_POINT_COUNT));
                return;
            }

            mSelectList.add(data);
            if (!btnConfirm.isEnabled()) {
                btnConfirm.setEnabled(true);
            }
        }
        mAdapter.setSelectData(mSelectList);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
            // 只有操作了数据才处理
            if (isOperateData && mSelectList != null && !mSelectList.isEmpty()) {
                MyDBSource.getInstance(this).deleteWaitPoint(BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(this)));
                MyDBSource.getInstance(this).insertWaitPoint(mSelectList);
                setResult(Activity.RESULT_OK);
            }
            finish();
        }
    }
}
