package com.bobomee.android.rxjavaexample.custom;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by bobomee on 16/5/3.
 */
public class CustomOperator extends RecyclerActivity {

    public void lift() {
        Observable.just(1, 2, 3).lift(new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                // 将事件序列中的 Integer 对象转换为 String 对象
                return new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext("" + integer);
                    }

                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }
                };
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                logger(s);
            }
        });
    }
}
