package com.bobomee.android.rxjavaexample;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class BasicIntroduction extends ToolBarActivity {

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
        defer();

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

    /**
     * Action0 是 RxJava 的一个接口，它只有一个方法 call()，这个方法是无参无返回值的；
     * 由于 onCompleted() 方法也是无参无返回值的，因此 Action0 可以被当成一个包装对象，
     * 将 onCompleted() 的内容打包起来将自己作为一个参数传入 subscribe() 以实现不完整定义的回调。
     * 这样其实也可以看做将 onCompleted() 方法作为参数传进了 subscribe()，相当于其他某些语言中的『闭包』。
     * <p>
     * Action1 也是一个接口，它同样只有一个方法 call(T param)，这个方法也无返回值，但有一个参数；
     * 与 Action0 同理，由于 onNext(T obj) 和 onError(Throwable error) 也是单参数无返回值的，
     * 因此 Action1 可以将 onNext(obj) 和 onError(error) 打包起来传入 subscribe() 以实现不完整定义的回调。
     * 事实上，虽然 Action0 和 Action1 在 API 中使用最广泛，但 RxJava 是提供了多个 ActionX 形式的接口 (例如 Action2, Action3) 的，
     * 它们可以被用以包装不同的无返回值的方法。
     */
    private void method2() {
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
// 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
//        observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
//        observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }


    /**
     * 线程控制Scheduler
     * <p>
     * 在不指定线程的情况下， RxJava 遵循的是线程不变的原则，
     * 即：在哪个线程调用 subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到 Scheduler （调度器）。
     * <p>
     * RxJava内置Scheduler
     * <p>
     * Schedulers.immediate(): 默认的 Scheduler,当前线程运行，即不指定线程。
     * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
     * 和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。
     * Schedulers.computation(): 计算所使用的 Scheduler。
     * 这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。
     * AndroidSchedulers.mainThread():它指定的操作将在 Android 主线程运行。
     * <p>
     * 注意：
     * 不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     * 不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     * <p>
     * subscribeOn(): 指定 subscribe() 所发生的线程,即Observable.OnSubscribe 被激活时所处的线程，或者叫做事件产生的线程。
     * <p>
     * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
     */

    private void method3() {
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


}
