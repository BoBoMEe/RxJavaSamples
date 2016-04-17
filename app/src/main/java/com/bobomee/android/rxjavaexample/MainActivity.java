package com.bobomee.android.rxjavaexample;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bobomee.android.common.util.ActivityUtil;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;
import com.zhy.base.adapter.recyclerview.OnItemClickListener;

import java.util.Arrays;

import butterknife.Bind;

public class MainActivity extends ToolBarActivity {

    String[] TITLES = {"Basic_introduction"};
    Class<?>[] CLZZS = {BasicIntroduction.class};

    CommonAdapter commonAdapter;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(commonAdapter = new CommonAdapter<String>(this, R.layout.recycler_view_tv_item, Arrays.asList(TITLES)) {
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(android.R.id.text1, s);
            }
        });

        commonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                ActivityUtil.startActivity(MainActivity.this, CLZZS[position]);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

}
