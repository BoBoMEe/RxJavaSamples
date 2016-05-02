package com.bobomee.android.rxjavaexample.Other;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/5/2.
 */
public class Other extends RecyclerActivity {

    public void amb() {
        Observable<Integer> delay3 = Observable.just(1, 2, 3).delay(3000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay2 = Observable.just(4, 5, 6).delay(2000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay1 = Observable.just(7, 8, 9).delay(1000, TimeUnit.MILLISECONDS);
        Observable.amb(delay1, delay2, delay3)
                .subscribe(this::logger);
    }

    public void all() {
        Observable.just(1, 2, 3, 4, 5, 6).all(integer -> integer < 6)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        logger(aBoolean + "");
                    }
                });
    }

    public void continers() {
        Observable.just(1, 2, 3).contains(3).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                logger(aBoolean + "");
            }
        });
    }

    public void isEmpty() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onCompleted();
            }
        }).isEmpty().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                logger(aBoolean + "");
            }
        });
    }

    public void defaultIfEmpty() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onCompleted();
            }
        }).defaultIfEmpty(10)
                .subscribe(
                        new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                logger(integer + "");
                            }
                        }
                );
    }

    public void sequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3), Observable.just(1, 2, 3))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        logger(aBoolean + "");
                    }
                });
    }

    public void skipUntil() {
        Observable.interval(1, TimeUnit.SECONDS).skipUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger(aLong);
                    }
                });
    }

    public void skipWhile() {
        Observable.interval(1, TimeUnit.SECONDS).skipWhile(aLong -> aLong < 5)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger(aLong);
                    }
                });
    }

    public void takeUntil() {
        Observable.interval(1, TimeUnit.SECONDS).takeUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger(aLong);
                    }
                });
    }

    public void takeWhile() {
        Observable.interval(1, TimeUnit.SECONDS).takeWhile(aLong -> aLong < 5)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger(aLong);
                    }
                });
    }

    public void concat() {
        Observable<Integer> obser1 = Observable.just(1, 2, 3);
        Observable<Integer> obser2 = Observable.just(4, 5, 6);
        Observable<Integer> obser3 = Observable.just(7, 8, 9);
        Observable.concat(obser1, obser2, obser3)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger(integer);
                    }
                });
    }

    public void count() {
        Observable.just(1, 2, 3).count().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger(integer);
            }
        });
    }

    public void reduce() {
        Observable.just(1, 2, 3, 4).reduce((x, y) -> x * y)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger(integer);
                    }
                });
    }

    public void collect() {
        Observable.just(1, 2, 3, 4).collect(() -> new ArrayList<>(), (integers, integer) -> integers.add(integer))
                .subscribe(new Action1<ArrayList<Object>>() {
                    @Override
                    public void call(ArrayList<Object> objects) {
                        logger(objects);
                    }
                });
    }

    private ConnectableObservable<Long> publishObserver() {
        Observable<Long> obser = Observable.interval(1, TimeUnit.SECONDS);
        obser.observeOn(Schedulers.newThread());
        return obser.publish();
    }

    public void publish() {
        ConnectableObservable<Long> obs = publishObserver();
        Subscription subscribe = obs.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                logger(aLong);
            }
        });

        obs.connect();

        addSubscription(subscribe);
    }

    public void refCount() {
        ConnectableObservable<Long> obs = publishObserver();
        Subscription subscribe = obs.refCount().subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                logger("refCount:" + aLong);
            }
        });

        obs.connect();

        addSubscription(subscribe);
    }

    public void replay() {
        Observable<Long> obser = Observable.interval(1, TimeUnit.SECONDS);
        obser.observeOn(Schedulers.newThread());
        ConnectableObservable<Long> replay = obser.replay(2);

        Subscription subscribe = replay.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                logger(aLong);
            }
        });

        replay.connect();

        addSubscription(subscribe);
    }

}


