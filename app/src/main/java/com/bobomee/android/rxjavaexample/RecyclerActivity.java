package com.bobomee.android.rxjavaexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobomee.android.rxjavaexample.util.ReflectUtil;
import com.orhanobut.logger.Logger;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;
import com.zhy.base.adapter.recyclerview.OnItemClickListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static java.util.Collections.sort;

/**
 * Created by bobomee on 2016/4/21.
 */
public class RecyclerActivity extends AppCompatActivity {

    CommonAdapter commonAdapter;
    List<Method> methods;
    CompositeSubscription mCompositeSubscription;

    @Bind(R.id.recycler)
    RecyclerView recycler;

    @Bind(R.id.result)
    TextView result;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    List<Method> getMethods() {
        List<Method> result = new ArrayList<>();
        Method[] reflects = ReflectUtil.getMethodNames(this.getClass());
        for (Method method : reflects) {
            if (!method.getName().contains("$") && Modifier.isPublic(method.getModifiers())) {
                result.add(method);
            }
        }
        sort(result, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        ButterKnife.bind(this);

        setUpRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ButterKnife.unbind(this);

        unsubscribe();
    }

    private void unsubscribe() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.clear();
        }
    }

    public void progressVisible() {
        if (null != progressBar)
            progressBar.setVisibility(View.VISIBLE);
    }

    public void progressGone() {
        if (null != progressBar)
            progressBar.setVisibility(View.GONE);
    }

    void setUpRecyclerView() {

        methods = getMethods();

        commonAdapter = new CommonAdapter<Method>(this,
                R.layout.recycler_view_tv_item, methods) {
            @Override
            public void convert(ViewHolder holder, Method m) {
                holder.setText(R.id.text, m.getName());
            }
        };

        commonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                ReflectUtil.invokeMethod(RecyclerActivity.this,
                        methods.get(position).getName());
            }


            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));

        recycler.setAdapter(commonAdapter);

    }

    public void logger(Object s) {
        Logger.d(String.valueOf(s));
        Observable.just(s).observeOn(AndroidSchedulers.mainThread()).subscribe(i -> {
            result.setText(result.getText() + "\n" + i);
        });

    }

    @OnClick(R.id.clear)
    public void clear() {
        result.setText("");
    }


    @OnClick(R.id.unsubscribe)
    public void unsubscribeClick() {
        unsubscribe();
    }


}
