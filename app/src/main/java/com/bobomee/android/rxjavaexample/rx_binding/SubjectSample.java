package com.bobomee.android.rxjavaexample.rx_binding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.bobomee.android.rxjavaexample.BaseActivity;
import com.bobomee.android.rxjavaexample.R;

import butterknife.Bind;
import butterknife.OnTextChanged;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static android.text.TextUtils.isEmpty;

/**
 * Created by bobomee on 16/5/5.
 */
public class SubjectSample extends BaseActivity {

    @Bind(R.id.double_binding_num1) EditText _number1;
    @Bind(R.id.double_binding_num2) EditText _number2;
    @Bind(R.id.double_binding_result) TextView _result;

    PublishSubject<Float> _resultEmitterSubject;
    Subscription _subscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subject_layout);


        init();
    }

    private void init() {
        _resultEmitterSubject = PublishSubject.create();
        _subscription = _resultEmitterSubject.asObservable().subscribe(new Action1<Float>() {
            @Override
            public void call(Float aFloat) {
                _result.setText(String.valueOf(aFloat));
            }
        });

        onNumberChanged();
        _number2.requestFocus();

        addSubscription(_subscription);
    }

    @OnTextChanged({ R.id.double_binding_num1, R.id.double_binding_num2 })
    public void onNumberChanged() {
        float num1 = 0;
        float num2 = 0;

        if (!isEmpty(_number1.getText().toString())) {
            num1 = Float.parseFloat(_number1.getText().toString());
        }

        if (!isEmpty(_number2.getText().toString())) {
            num2 = Float.parseFloat(_number2.getText().toString());
        }

        _resultEmitterSubject.onNext(num1 + num2);
    }
}
