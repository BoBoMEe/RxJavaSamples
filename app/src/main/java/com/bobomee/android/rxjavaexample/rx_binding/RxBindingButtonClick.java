package com.bobomee.android.rxjavaexample.rx_binding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bobomee.android.rxjavaexample.BaseActivity;
import com.bobomee.android.rxjavaexample.R;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by bobomee on 16/5/5.
 */
public class RxBindingButtonClick extends BaseActivity {

    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.btn_click)
    Button btnClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_throttle_first);
    }

    @OnClick(R.id.btn_click)
    public void btnClick(){
        RxView.clicks(btnClick)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(RxBindingButtonClick.this,"Click",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
