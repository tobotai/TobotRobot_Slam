package com.tobot.disinfect.module.set.wait;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tobot.common.base.BaseFragment;
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
 * @date 2020/5/29
 */
public class WaitPointFragment extends BaseFragment implements View.OnClickListener {
    private static final int SPAN_COUNT = 5;
    private TaskCreateAdapter mTaskCreateAdapter;

    public static WaitPointFragment newInstance() {
        return new WaitPointFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wait_point;
    }

    @Override
    protected void initView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.btn_add_wait_point).setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));
        int spaceSize = getResources().getDimensionPixelSize(R.dimen.recycler_item_space_size);
        recyclerView.addItemDecoration(new GridDecoration(spaceSize, spaceSize, spaceSize, spaceSize));
        mTaskCreateAdapter = new TaskCreateAdapter(getActivity(), R.layout.recycler_item_task_create);
        recyclerView.setAdapter(mTaskCreateAdapter);
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
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_wait_point) {
            List<LocationBean> locationList = BaseData.getInstance().getOriginalData();
            if (locationList != null && !locationList.isEmpty()) {
                Intent intent = new Intent(getActivity(), WaitPointAddActivity.class);
                intent.putParcelableArrayListExtra(BaseConstant.DATA_KEY, (ArrayList<? extends Parcelable>) locationList);
                startActivityForResult(intent, 1);
                return;
            }
            showToastTips(getString(R.string.no_location_tips));
        }
    }

    private void setData() {
        mTaskCreateAdapter.setData(MyDBSource.getInstance(getActivity()).queryWaitPoint(BaseData.getInstance().getMapNum(BaseData.getInstance().getSelectMapName(getActivity()))));
    }
}
