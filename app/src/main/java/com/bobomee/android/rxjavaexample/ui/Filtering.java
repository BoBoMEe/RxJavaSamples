package com.bobomee.android.rxjavaexample.ui;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/4/28.
 */
public class Filtering extends RecyclerActivity {

    public void throttleWithTimeout() {
        Subscription subscribe = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(i);
                    }
                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> logger("throttleWithTimeout:" + i));
        addSubscription(subscribe);

    }

    public void debounce() {
        Subscription subscribe = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).debounce(integer -> {
            logger(integer);
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    if (integer % 2 == 0 && !subscriber.isUnsubscribed()) {
                        logger("complete:" + integer);
                        subscriber.onNext(integer);
                        subscriber.onCompleted();
                    }
                }
            });
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> logger("debounce:" + i));
        addSubscription(subscribe);
    }
}
