package com.bobomee.android.rxjavaexample.rx_samples.from_rxjava_android_samples;

import com.bobomee.android.rxjavaexample.RecyclerActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/5/8.
 */
public class CustomMerge extends RecyclerActivity {
    public void mergeObserver() {
        Observable.merge(getData1(), getData2())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<Contacter>, Observable<Contacter>>() {
                    @Override
                    public Observable<Contacter> call(List<Contacter> contacters) {
                        return Observable.from(contacters);
                    }
                })
                .subscribe(new Observer<Contacter>() {
                    @Override
                    public void onCompleted() {
                        logger("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger("onError" + e.toString());
                    }

                    @Override
                    public void onNext(Contacter contacter) {
                        logger(contacter);
                    }
                })
        ;


    }


    private Observable<List<Contacter>> getData1() {
        return Observable.create(new Observable.OnSubscribe<List<Contacter>>() {
            @Override
            public void call(Subscriber<? super List<Contacter>> subscriber) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ArrayList<Contacter> contacters = new ArrayList<>();
                contacters.add(new Contacter("net:Zeus"));
                contacters.add(new Contacter("net:Athena"));
                contacters.add(new Contacter("net:Prometheus"));
                subscriber.onNext(contacters);
                subscriber.onCompleted();
            }
        });
    }


    private Observable<List<Contacter>> getData2() {
        return Observable.create(new Observable.OnSubscribe<List<Contacter>>() {
            @Override
            public void call(Subscriber<? super List<Contacter>> subscriber) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<Contacter> datas = new ArrayList<>();
                datas.add(new Contacter("location:" + "张三"));
                datas.add(new Contacter("location:" + "李四"));
                datas.add(new Contacter("location:" + "王五"));

                subscriber.onNext(datas);
                subscriber.onCompleted();
            }
        });
    }

    class Contacter {

        private String name;

        public Contacter(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Contacter{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}
