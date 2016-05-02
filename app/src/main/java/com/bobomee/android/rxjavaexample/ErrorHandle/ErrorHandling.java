package com.bobomee.android.rxjavaexample.ErrorHandle;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Created by bobomee on 16/5/1.
 */
public class ErrorHandling extends RecyclerActivity {

    private Observable<String> createObserver(Boolean createExcetion) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i <= 6; i++) {
                    if (i < 3) {
                        subscriber.onNext("onNext:" + i);
                    } else if (createExcetion) {
                        subscriber.onError(new Exception("Exception"));
                    } else {
                        subscriber.onError(new Throwable("Throw error"));
                    }
                }
            }
        });
    }

    public void onErrorReturn() {

        createObserver(false).onErrorReturn(throwable -> "onErrorReturn")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        logger("onErrorReturn-onCompleted\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onErrorReturn-onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        logger("onErrorReturn-onNext:" + s);
                    }
                });
    }

    public void onErrorResumeNext() {

        createObserver(false).onErrorResumeNext(Observable.just("7", "8", "9"))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        logger("onErrorResume-onCompleted\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onErrorResume-onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        logger("onErrorResume-onNext:" + s);
                    }
                });
    }

    private Observable<String> onExceptionResumeObserver(boolean isException) {
        return createObserver(isException).onExceptionResumeNext(Observable.just("7", "8", "9"));
    }

    public void onExceptionResumeTrue() {
        onExceptionResumeObserver(true).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                logger("onException-true-onCompleted\n");
            }

            @Override
            public void onError(Throwable e) {
                logger("onException-true-onError:" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                logger("onException-true-onNext:" + s);
            }
        });
    }

    public void onExceptionResumeFalse() {
        onExceptionResumeObserver(false).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                logger("onException-false-onCompleted\n");
            }

            @Override
            public void onError(Throwable e) {
                logger("onException-false-onError:" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                logger("onException-false-onNext:" + s);
            }
        });
    }

    public void retry() {
        createObserver(false).retry(2)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        logger("retry-onCompleted\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("retry-onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        logger("retry-onNext:" + s);
                    }
                });
    }

    public void retryWhen() {
        Observable.create((Subscriber<? super String> s) -> {
            logger("subscribing");
            s.onError(new RuntimeException("always fails"));
        }).retryWhen(attempts -> {
            return attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
                logger("delay retry by " + i + " second(s)");
                return Observable.timer(i, TimeUnit.SECONDS);
            });
        }).toBlocking().forEach(this::logger);
    }

}
