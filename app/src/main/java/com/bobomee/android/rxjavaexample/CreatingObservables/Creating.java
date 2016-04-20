package com.bobomee.android.rxjavaexample.CreatingObservables;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bobomee.android.rxjavaexample.R;
import com.bobomee.android.rxjavaexample.ToolBarActivity;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bobomee on 16/4/16.
 */
public class Creating extends ToolBarActivity {

    //设定查询目录
    String PATh = "/mnt/sdcard/DCIM/Screenshots";
    File[] floders = new File[]{
            new File(PATh)
    };

    @Override
    protected int provideContentViewId() {
        return R.layout.basic_introduction;
    }

    @Override
    public boolean canBack() {
        return true;
    }


    @OnClick({R.id.button_nomal, R.id.button_rx, R.id.button_rx_})
    public void method(View view) {

        switch (view.getId()) {
            case R.id.button_nomal: {

                doNomal();
            }
            break;
            case R.id.button_rx: {

                doRxjava();
            }
            break;
            case R.id.button_rx_: {
                doRxJavaMethod();
            }
            break;
            default:
                break;
        }
    }

    //常规做法
    private void doNomal() {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (File floder : floders) {
                    File[] files = floder.listFiles();

                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".png")) {

                            final String path = file.getAbsolutePath();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d(path);
                                }
                            });

                        }
                    }
                }
            }
        });


    }

    //Rxjava做法
    private void doRxjava() {

//      nolambda();


        uselambda();
    }

    private void uselambda() {
        rx.Observable.from(floders)
                .flatMap(file -> rx.Observable.from(file.listFiles()))
                .filter(file -> file.isFile() && file.getName().endsWith(".png"))
                .map(File::getAbsolutePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Logger.d(s));
    }

    private void nolambda() {
        rx.Observable.from(floders)
                .flatMap(new Func1<File, rx.Observable<File>>() {
                    @Override
                    public rx.Observable<File> call(File file) {
                        return rx.Observable.from(file.listFiles());
                    }
                })
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return file.isFile() && file.getName().endsWith(".png");
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return file.getAbsolutePath();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.d(s);
                    }
                });
    }

    @NonNull
    private Observable<String> createStringObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello");
                subscriber.onNext("Hi");
                subscriber.onNext("Aloha");
                subscriber.onCompleted();
                subscriber.onError(new Throwable());
            }
        });
    }

    @NonNull
    private Observer<String> createStringObserver() {
        return new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Logger.d("Item: " + s);
            }

            @Override
            public void onCompleted() {
                Logger.d("Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("Error!");
            }
        };
    }

    @NonNull
    private Observer<Integer> createIntegerObserver() {
        return new Subscriber<Integer>() {
            @Override
            public void onNext(Integer s) {
                Logger.d("Item: " + s);
            }

            @Override
            public void onCompleted() {
                Logger.d("Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("Error!");
            }
        };
    }

    // TODO: 16/4/17  测试入口
    //RxJava逐个方法测试
    private void doRxJavaMethod() {
        timer();

    }

    //Creating Observables
    private void create() {
        //1.观察者
        Observer<String> subscriber = createStringObserver();
        //2.被观察者
        Observable<String> observable = createStringObservable();
        //3.订阅
        observable.subscribe(subscriber);
    }

    private void just() {
        //1.观察者
        Observer<String> subscriber = createStringObserver();
        //2.被观察者
        Observable observable = Observable.just("just", "test", "just");
        //3:订阅:
        observable.subscribe(subscriber);
    }

    private void from() {
        //1.观察者
        Observer<String> subscriber = createStringObserver();
        //2.被观察者
        String[] words = {"from", "test", "from"};
        Observable observable = Observable.from(words);
        //3:订阅:
        observable.subscribe(subscriber);
    }

    private void range() {
        //1.观察者
        Observer<Integer> subscriber = createIntegerObserver();
        //2.被观察者
        Observable observable = Observable.range(10, 5);
        //3:订阅:
        observable.subscribe(subscriber);
    }

    private void defer() {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(System.currentTimeMillis() + "");
            }
        })
                .subscribe(createStringObserver());

//        Observable.just(System.currentTimeMillis()).subscribe(s -> Logger.d(s + ""));

    }

    /**
     * 不完整定义的回调
     */
    private void action() {
        //1.观察者

        Action1<String> onNextAction = new Action1<String>() {
            // onNext()
            @Override
            public void call(String s) {
                Logger.d(s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
                Logger.d(throwable.toString());
            }
        };
        Action0 onCompletedAction = new Action0() {
            // onCompleted()
            @Override
            public void call() {
                Logger.d("completed");
            }
        };


        //2.被观察者
        rx.Observable<String> observable = Observable.just("just", "just", "just", "just");

        //3.订阅
        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }


    private void scheduler() {
        int drawableRes = R.mipmap.ic_launcher;
        ImageView imageView = new ImageView(this);
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(drawableRes);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onNext(Drawable drawable) {
                        imageView.setImageDrawable(drawable);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("Error!");
                    }
                });
    }

    private void interval() {
        Observable.interval(1, TimeUnit.SECONDS).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError" + e.getMessage());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Logger.d("interval:" + aLong);
                    }
                });
    }


    private void repeat() {
        Observable.just(1, 2, 3, 4, 5).repeat(5).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> Logger.d(integer + ""));
    }

    private void timer() {
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(l -> Logger.d(l + ""));
    }


}
