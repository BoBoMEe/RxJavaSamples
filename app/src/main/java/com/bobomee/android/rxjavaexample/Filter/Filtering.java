package com.bobomee.android.rxjavaexample.Filter;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
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
        }).subscribeOn(Schedulers.computation())
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> logger("throttleWithTimeout:" + i));
        addSubscription(subscribe);

    }

    public void debounce() {
        Subscription subscribe = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).debounce(integer -> {
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    //如果%2==0，则发射数据，且调用了onCompleted
                    if (integer % 2 == 0 && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(integer);
                        subscriber.onCompleted();
                    }
                }
            });
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger("debounce:" + integer);
                    }
                });
        addSubscription(subscribe);
    }

    public void distinct() {
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinct()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        logger("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        logger("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        logger("Sequence complete.");
                    }
                });
    }

    public void distinctUntilChanged() {
        Observable.just(1, 2, 3, 3, 3, 1, 2, 3, 3)
                .distinctUntilChanged()
                .subscribe(integer -> logger("UntilChanged:" + integer));
    }

    public void elementAt() {
        Observable.just(0, 1, 2, 3, 4, 5).elementAt(2)
                .subscribe(i -> logger("elementAt:" + i));
    }

    public void filter() {
        Observable.just(1, 2, 3, 4, 5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer item) {
                        return (item < 4);
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                logger("Next: " + item);
            }

            @Override
            public void onError(Throwable error) {
                logger("Error: " + error.getMessage());
            }

            @Override
            public void onCompleted() {
                logger("Sequence complete.");
            }
        });
    }


    public void first() {
        BlockingObservable<Integer> integerBlockingObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!subscriber.isUnsubscribed()) {
                        logger("onNext:" + i);
                        subscriber.onNext(i);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).toBlocking();


        Integer first = integerBlockingObservable.first(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 3;
            }
        });

        logger(first);
    }

    public void takeLast() {
        Observable.just(1, 2, 3, 4, 5, 6, 7).takeLast(2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        logger("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        logger("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        logger("Sequence complete.");
                    }
                });
    }

    public void skip() {
        Observable.just(0, 1, 2, 3, 4, 5).skip(2).subscribe(i -> logger("Skip:" + i));
    }

    public void take() {
        Observable.just(0, 1, 2, 3, 4, 5).take(2).subscribe(i -> logger("Take:" + i));
    }

    public void sample() {
        createObserver().sample(1000, TimeUnit.MILLISECONDS)
                .subscribe(i -> logger("sample:" + i));
    }

    public void throttleFirst() {
        createObserver().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(i -> logger("throttleFirst:" + i));
    }

    private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });
    }

    public void ignoreElements() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8).ignoreElements()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        logger("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        logger("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        logger("Sequence complete.");
                    }
                });
    }
}
