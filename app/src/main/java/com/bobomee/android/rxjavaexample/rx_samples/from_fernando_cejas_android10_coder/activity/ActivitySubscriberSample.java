package com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bobomee.android.rxjavaexample.BaseActivity;
import com.bobomee.android.rxjavaexample.R;
import com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.adapter.ElementsAdapter;
import com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.data.DataManager;
import com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.data.NumberGenerator;
import com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.data.StringGenerator;
import com.bobomee.android.rxjavaexample.rx_samples.from_fernando_cejas_android10_coder.executor.JobExecutor;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by bobomee on 16/5/7.
 */
public class ActivitySubscriberSample extends BaseActivity implements ElementsAdapter.ElementAddedListener {
    @Bind(android.R.id.list)
    RecyclerView rv_elements;

    private DataManager dataManager;
    private ElementsAdapter adapter;

    private Subscription subscription;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, ActivitySubscriberSample.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_subscriber);

        initialize();
        fillData();
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    private void initialize() {
        subscription = Subscriptions.unsubscribed();
        dataManager = new DataManager(new StringGenerator(), new NumberGenerator(),
                JobExecutor.getInstance());
        adapter = new ElementsAdapter(this, this);
        rv_elements.setLayoutManager(new LinearLayoutManager(this));
        rv_elements.setAdapter(this.adapter);
    }

    private void fillData() {
        subscription = this.dataManager.elements().subscribe(this.adapter);
    }

    @OnClick(android.R.id.button1) void onAddElementClick() {
        subscription = this.dataManager.newElement().subscribe(this.adapter);
        Toast.makeText(this, "Element added using an observable!!!", Toast.LENGTH_SHORT).show();
    }

    @Override public void onElementAdded() {
        rv_elements.smoothScrollToPosition(adapter.getItemCount());
    }
}
