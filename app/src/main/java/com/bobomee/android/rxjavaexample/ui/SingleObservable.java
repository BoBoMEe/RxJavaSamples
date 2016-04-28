package com.bobomee.android.rxjavaexample.ui;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import rx.SingleSubscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by bobomee on 16/4/27.
 */
public class SingleObservable extends RecyclerActivity {

    public void create() {
        rx.Single.create(new rx.Single.OnSubscribe<String>() {
            @Override
            public void call(SingleSubscriber<? super String> singleSubscriber) {
                singleSubscriber.onSuccess("Hello");
            }
        })
                .flatMap(new Func1<String, rx.Single<Integer>>() {
                    @Override
                    public rx.Single<Integer> call(String s) {
                        return rx.Single.just(s.hashCode());
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger(integer);
                    }
                });
    }


    public void just() {
        rx.Single.just(5).subscribe(this::logger);
    }

}
