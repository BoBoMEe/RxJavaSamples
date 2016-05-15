package com.bobomee.android.rxjavaexample.rx_samples.from_rxjava_android_samples;

import com.bobomee.android.rxjavaexample.RecyclerActivity;
import com.orhanobut.logger.Logger;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by bobomee on 16/5/8.
 */
public class CustomFunc1 extends RecyclerActivity {

    private int _getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int) (TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public void repeatWithDelay() {

        addSubscription(
                Observable.just(1)
                        .repeatWhen(new RepeatWithDelay(8, 1000))
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                logger(String.format(Locale.US, "Start increasingly delayed polling now time: [xx:%02d]",
                                        _getSecondHand()));
                            }
                        })
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onCompleted() {
                                logger("onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                logger("arrrr. Error" + e.toString());
                            }

                            @Override
                            public void onNext(Integer integer) {
                                logger(String.format(Locale.US, "Executing polled task [%d] now time : [xx:%02d]",
                                        integer, _getSecondHand()));
                            }
                        })
        );
    }


    //====================================== ----------

    // CAUTION:
    // --------------------------------------
    // THIS notificationHandler class HAS NO BUSINESS BEING non-static
    // I ONLY did this cause i wanted access to the `_log` method from inside here
    // for the purpose of demonstration. In the real world, make it static and LET IT BE!!

    // It's 12am in the morning and i feel lazy dammit !!!

    //public static class RepeatWithDelay
    private class RepeatWithDelay
            implements Func1<Observable<? extends Void>, Observable<?>> {

        private final int _repeatLimit;
        private final int _pollingInterval;
        private int _repeatCount = 1;

        RepeatWithDelay(int repeatLimit, int pollingInterval) {
            _pollingInterval = pollingInterval;
            _repeatLimit = repeatLimit;
        }

        // this is a notificationhandler, all we care about is
        // the emission "type" not emission "content"
        // only onNext triggers a re-subscription

        @Override
        public Observable<?> call(Observable<? extends Void> inputObservable) {

            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence

            return inputObservable.flatMap(new Func1<Void, Observable<?>>() {
                @Override
                public Observable<?> call(Void blah) {


                    if (_repeatCount >= _repeatLimit) {
                        // terminate the sequence cause we reached the limit
                        Logger.i("Completing sequence");
                        return Observable.empty();
                    }

                    // since we don't get an input
                    // we store state in this handler to tell us the point of time we're firing
                    _repeatCount++;

                    return Observable.timer(_repeatCount * _pollingInterval,
                            TimeUnit.MILLISECONDS);
                }
            });
        }
    }


}
