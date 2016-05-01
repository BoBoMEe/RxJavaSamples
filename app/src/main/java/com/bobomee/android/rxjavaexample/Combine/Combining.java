package com.bobomee.android.rxjavaexample.Combine;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/4/30.
 */
public class Combining extends RecyclerActivity {


    public void merge() {
        Observable<Integer> odds = Observable.just(1, 3, 5).subscribeOn(Schedulers.io());
        Observable<Integer> evens = Observable.just(2, 4, 6);

        Observable.merge(odds, evens)
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

    public void mergeDelayError() {
        Observable.mergeDelayError(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i == 3) {
                        subscriber.onError(new Throwable("error"));
                    }
                    subscriber.onNext(i);
                }
            }
        }), Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(5 + i);
                }
                subscriber.onCompleted();
            }
        })).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                logger("onCompleted");
            }

            @Override
            public void onNext(Integer integer) {
                logger("mergeDelayError:" + integer);
            }
        });
    }

    private Observable<String> createZipObserver(int index) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i <= index; i++) {
                    logger("emitted:" + index + "-" + i);
                    subscriber.onNext(index + "-" + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public void zipWith() {
        createZipObserver(2).zipWith(createZipObserver(3), new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                return s + "-" + s2;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                logger("zipWith:" + s + "\n");
            }
        });
    }

    public void zip() {
        Observable
                .zip(createZipObserver(2), createZipObserver(3), createZipObserver(4), new Func3<String, String, String, String>() {
                    @Override
                    public String call(String s, String s2, String s3) {
                        return s + "-" + s2 + "-" + s3;
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                logger("zip:" + s + "\n");
            }
        });
    }

    private Observable<String> createJoinObserver() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i < 5; i++) {
                    subscriber.onNext("Right-" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public void join() {
        Observable.just("Left-").join(createJoinObserver(),
                integer -> Observable.timer(3000, TimeUnit.MILLISECONDS),
                integer -> Observable.timer(2000, TimeUnit.MILLISECONDS),
                (i, j) -> i + j
        ).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                logger("join:" + s + "\n");
            }
        });
    }

    public void groupJoin() {
        Observable.just("Left-")
                .groupJoin(createJoinObserver(),
                        s -> Observable.timer(3000, TimeUnit.MILLISECONDS),
                        s -> Observable.timer(2000, TimeUnit.MILLISECONDS),
                        (s, stringObservable) -> stringObservable.map(str -> s + str))
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                logger("groupJoin:" + s + "\n");
                            }
                        });
                    }
                });
    }


    private Observable<Integer> createCombineLatest(int index) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i < 3; i++) {
                    subscriber.onNext(i * index);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }


    public void combineLatest() {
        Observable.combineLatest(createCombineLatest(1), createCombineLatest(2), (num1, num2) -> {
            logger("left:" + num1 + " right:" + num2);
            return num1 + num2;
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger("CombineLatest:" + integer);
            }
        });
    }

    List<Observable<Integer>> list = new ArrayList<>();

    public void combineList() {
        for (int i = 1; i < 3; i++) {
            list.add(createCombineLatest(i));
        }
        Observable.combineLatest(list, args -> {
            int temp = 0;
            for (Object i : args) {
                logger(i);
                temp += (Integer) i;
            }
            return temp;
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger("combineList:" + integer);
            }
        });
    }

    private Observable<String> createSwitchObserver(int index) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i < 5; i++) {
                    subscriber.onNext(index + "-" + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public void switchObserver()

    {
        Observable.switchOnNext(Observable.create(
                new Observable.OnSubscribe<Observable<String>>() {
                    @Override
                    public void call(Subscriber<? super Observable<String>> subscriber) {
                        for (int i = 1; i < 3; i++) {
                            subscriber.onNext(createSwitchObserver(i));
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        logger("switch:" + s);
                    }
                });
    }

    public void startWith() {
        Observable.just(1, 2, 3).startWith(-1, 0)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger("startWith:" + integer);
                    }
                });
    }

}
