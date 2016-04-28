package com.bobomee.android.rxjavaexample.ui;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by bobomee on 16/4/28.
 */
public class Subjects extends RecyclerActivity {

    public void publishSubject() {
        PublishSubject<String> stringPublishSubject = PublishSubject.create();
        Subscription subscriptionPrint = stringPublishSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                logger("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("Oh,no!Something wrong happened!");
            }

            @Override
            public void onNext(String message) {
                logger(message);
            }
        });
        stringPublishSubject.onNext("Hello World");
    }


    public void publishSubjectAdvance() {
        final PublishSubject<Boolean> subject = PublishSubject.create();

        subject.subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                logger("subject : Observable Completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("subject : Observable onError");
            }

            @Override
            public void onNext(Boolean aBoolean) {
                logger("subject : Observable onNext:" + aBoolean);
            }
        });


        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("Observable Completed");
                subject.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                logger(" Observable onError");
                subject.onError(e);
            }

            @Override
            public void onNext(Integer integer) {
                logger(" Observable onNext:" + integer);
                subject.onNext(integer % 2 == 0);
            }
        });
    }


    public void behaviorSubject() {
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create(1);

        Subscription subscriptionPrint = behaviorSubject.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("Oh,no!Something wrong happened!");
            }

            @Override
            public void onNext(Integer message) {
                logger(message);
            }
        });
        Observable.range(10, 5).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                behaviorSubject.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                behaviorSubject.onError(e);
            }

            @Override
            public void onNext(Integer integer) {
                behaviorSubject.onNext(integer);
            }
        });
    }

    public void replaySubject() {
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();

        Observer observer1 = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("observer1:Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("observer1:Oh,no!Something wrong happened!");
            }

            @Override
            public void onNext(Integer message) {
                logger(message);
            }
        };

        Observer observer2 = new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("observer2:Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("observer2:Oh,no!Something wrong happened!");
            }

            @Override
            public void onNext(Integer message) {
                logger(message);
            }
        };

        Subscription subscriptionPrint1 = replaySubject.subscribe(observer1);
        Subscription subscriptionPrint2 = replaySubject.subscribe(observer2);


        Observable.just(1, 2, 3, 4, 5).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                replaySubject.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                replaySubject.onError(e);
            }

            @Override
            public void onNext(Integer integer) {
                replaySubject.onNext(integer);
            }
        });

        addSubscription(subscriptionPrint1);
        addSubscription(subscriptionPrint2);
    }

    public void asyncSubject() {
        AsyncSubject<Integer> asyncSubject = AsyncSubject.create();

        asyncSubject.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("subject : Observable Completed");
            }

            @Override
            public void onError(Throwable e) {
                logger("subject : Observable onError");
            }

            @Override
            public void onNext(Integer aBoolean) {
                logger("subject : Observable onNext:" + aBoolean);
            }
        });


        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                logger("Observable Completed");
                asyncSubject.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                logger(" Observable onError");
                asyncSubject.onError(e);
            }

            @Override
            public void onNext(Integer integer) {
                logger(" Observable onNext:" + integer);
                asyncSubject.onNext(integer);
            }
        });
    }

}
