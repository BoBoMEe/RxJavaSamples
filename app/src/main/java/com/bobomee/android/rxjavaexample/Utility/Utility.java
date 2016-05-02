package com.bobomee.android.rxjavaexample.Utility;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Notification;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.schedulers.Timestamped;

/**
 * Created by bobomee on 16/5/1.
 */
public class Utility extends RecyclerActivity {

    private Observable<Long> createDelayObserver(int index) {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                for (int i = 1; i <= index; i++) {
                    subscriber.onNext(getCurrentTime());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    private long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    public void delay() {
        createDelayObserver(2).delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger("delay:" + (getCurrentTime() - aLong));
                    }
                });
    }

    public void delaySubscription() {
        createDelayObserver(2).delaySubscription(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logger("delaySubscription:" + (getCurrentTime() - aLong));
                    }
                });
    }

    public void meterialize() {
        Observable.just(1, 2, 3).materialize()
                .subscribe(new Action1<Notification<Integer>>() {
                    @Override
                    public void call(Notification<Integer> integerNotification) {
                        logger("kind:" + integerNotification.getKind() + "--value:" + integerNotification.getValue());
                    }
                });
    }

    public void demeterialize() {
        Observable.just(1, 2, 3).materialize()
                .dematerialize().subscribe(i -> logger("meterialize:" + i));
    }

    private Observable<Integer> createTimeObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public void timeInterval() {
        createTimeObserver().timeInterval()
                .subscribe(new Observer<TimeInterval<Integer>>() {
                    @Override
                    public void onCompleted() {
                        logger("onCompleted!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onError!");
                    }

                    @Override
                    public void onNext(TimeInterval<Integer> integerTimeInterval) {
                        logger("onNext:" + integerTimeInterval.getIntervalInMilliseconds());
                    }
                });
    }


    public void timeStamp() {
        createTimeObserver().timestamp()
                .subscribe(new Observer<Timestamped<Integer>>() {
                    @Override
                    public void onCompleted() {
                        logger("onCompleted!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onError!");
                    }

                    @Override
                    public void onNext(Timestamped<Integer> integerTimestamped) {
                        logger("onNext:" + integerTimestamped.getTimestampMillis());
                    }
                });
    }

    private Observable<Integer> createTimeOutObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });
    }

    public void timeOut() {

        createTimeOutObserver().timeout(100, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        logger("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        logger("timeOut:" + integer);
                    }
                });
    }

    public void timeOutObserver() {
        createTimeOutObserver().timeout(100, TimeUnit.MILLISECONDS, Observable.just(5, 6))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger(integer);
                    }
                });
    }

    public void using() {
        Subscription subscribe = Observable.using(new Func0<Animal>() {
            @Override
            public Animal call() {
                return new Animal();
            }
        }, new Func1<Animal, Observable<? extends Long>>() {
            @Override
            public Observable<? extends Long> call(Animal animal) {
                return Observable.timer(5000, TimeUnit.MILLISECONDS);
            }
        }, new Action1<Animal>() {
            @Override
            public void call(Animal animal) {
                animal.relase();
            }
        }).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                logger("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                logger("onError");
            }

            @Override
            public void onNext(Long aLong) {
                logger("onNext" + aLong);
            }
        });

        addSubscription(subscribe);
    }

    private class Animal {
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                logger("animal eat");
            }
        };

        public Animal() {
            logger("create animal");
            Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .subscribe(subscriber);
        }

        public void relase() {
            logger("animal released");
            subscriber.unsubscribe();
        }
    }

    public void toList() {
        Observable.just(1, 2, 3, 4, 5, 6).toList().subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                logger(integers);
            }
        });
    }


}
