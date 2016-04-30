## RxJava学习基础

RxJava是一个实现异步操作的库，采用链式掉用来实现响应式编程，使逻辑代码更加清晰。是ReactiveX的Java版本实现。

RxJava类似观察者模式，Observables (被观察者)和 Observers(Subscribers) (观察者)通过 subscribe(订阅)方法实现订阅关系
Observables 在需要的时候发出事件来通知 Observers(Subscribers).

类似Android中Button的点击事件的监听：
Button -> 被观察者、OnClickListener -> 观察者、setOnClickListener() -> 订阅，onClick() -> 事件

和观察者模式不同的是 ：
一个"热"的Observable可能一创建完就开始发射数据，因此所有后续订阅它的观察者可能从序列中间的某个位置开始接受数据（会丢失一些数据）。
一个"冷"的Observable会一直等待，直到有观察者订阅它才开始发射数据，因此这个观察者可以确保会收到整个数据序列。

## RxJava回调方法

RxJava中定义了三种回调方法：
  onNext()：相当于 onClick() / onEvent()
  onCompleted(): 事件队列完结，时间队列中没有 新的 onNext() 发出时触发
  onError(): 事件队列异常，事件处理过程出异常时，onError() 会被触发，同时队列自动终止，不再有事件发出
  onCompleted() 和 onError() 二者互斥，在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只会触发一个
  
## 基本实现

创建一个RxJava掉用实现需要三个步骤
   1. 创建观察者 Observer或者Subscriber
   2. 创建被观察者 Observable
   3. 订阅subscribe
       
 HelloWorld:

```java
@NonNull
    private Observer<String> createObserver() {
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
```

```java
     @NonNull
         private Observable<String> createObservable() {
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
```

   
>这里传入了一个 OnSubscribe 对象作为参数.
OnSubscribe 会被存储在返回的 Observable 对象中，它的作用相当于一个计划表，
当 Observable 被订阅的时候，OnSubscribe 的 call() 方法会自动被调用，事件序列就会依照设定依次触发
         

>上面的定义就是：
观察者Subscriber 将会被调用三次 onNext() 和一次 onCompleted(),其中onError和onCompleted互斥。
被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
  
```java
    //Creating Observables
       private void create() {
           //1.观察者
           Observer<String> subscriber = createObserver();
           //2.被观察者
           Observable<String> observable = createObservable();
           //3.订阅
           observable.subscribe(subscriber);
       }
```

>流式 API 的设计 使得 这里看起来像是:被观察者 订阅了 观察者
 
       
> 其中Subscriber是Observer的抽象类，在使用过程中，Observer 也总是会先被转换成一个 Subscriber 再使用
 onStart()：Subscriber类中新增方法，在subscribe所在线程执行，用于一些准备工作，如果需要指定线程可以使用doOnSubscribe()方法
 unsubscribe()：Subscriber类中新增方法，是Subscription接口中的方法，Subscriber实现它，用于取消订阅，可以放置内存泄漏
 isUnsubscribed()：Subscription接口中的方法，用于判断订阅状态，一般在使用unsubscribe()时先判断一下

## Just、From

 just 和 from操作符用来快捷创建事件队列。
 其中just(T...):将传入的参数依次发送出来(一次来将整个的数组发射出去)。
 而from(T[])/from(Iterable<? extends T>) : 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来（发射T.lenght次）。
 
 - from操作符可以转换Future、Iterable和数组,对于Iterable和数组，产生的Observable会发射Iterable或数组的每一项数据.
 对于Future，它会发射Future.get()方法返回的单个数据
 
 - 如果你传递null给Just，它会返回一个发射null值的Observable,如果需要空Observable你应该使用Empty操作符.
 
 //1.观察者
```java
 Observer<String> subscriber = createObserver();
```

//2. 被观察者
```java
//just
Observable observable = Observable.just("just", "test", "just");
```

>上面代码将会依次调用：
            onNext("Hello");
            onNext("Hi");
            onNext("Aloha");
            onCompleted();或者程序出现异常掉用onError

```java
//from
String[] words = {"from", "test", "from"};
Observable observable = Observable.from(words);
```

//3. 订阅
```java
observable.subscribe(subscriber);
```

## Range 
Range操作符发射一个范围内的有序整数序列，你可以指定范围的起始和长度。它接受两个参数，一个是范围的起始值，一个是范围的数据的数目。
如果你将第二个参数设为0，将导致Observable不发射任何数据（如果设置为负数，会抛异常）。

```java
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
```

```java
private void range() {
        //1.观察者
        Observer<Integer> subscriber = createIntegerObserver();
        //2.被观察者
        Observable observable = Observable.range(10, 5);
        //3:订阅:
        observable.subscribe(subscriber);
    }
```
>上述代码将会打印10、11、12、13、14、Completed!

## Defer 
Defer操作符只有当有Subscriber来订阅的时候才会创建一个新的Observable对象,
每次订阅都会得到一个刚创建的最新的Observable对象，确保Observable对象里的数据是最新的.
Defer操作符会一直等待直到有观察者订阅它，然后它使用Observable工厂方法生成一个Observable。
如下代码就会打印当前实时时间

```java
 private void defer() {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(System.currentTimeMillis() + "");
            }
        })
                .subscribe(createStringObserver());
    }
```

> 和 just不同的是，just可以将数字、字符串、数组、Iterate对象转为Observable对象发射出去，但值是创建的时候就不变了的。

## Action

Action是RxJava中专门用于处理 （无返回值） 的不完整回调的，RxJava 会自动根据定义创建出 Subscriber 。
相当于一个包装对象，比如Action0是无参无返回值的 ，只有一个方法 call()，常用于包装onCompleted(),
Action1带一个参数，只有一个方法 call(T param)，常用于包装onNext(obj) 和 onError(error)，
RxJava 提供了多个 ActionX 形式的接口 (例如 Action2, Action3) 

```java
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
```

subscriber相关源码

```java
public final Subscription subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError, final Action0 onComplete) {
        if (onNext == null) {
            throw new IllegalArgumentException("onNext can not be null");
        }
        if (onError == null) {
            throw new IllegalArgumentException("onError can not be null");
        }
        if (onComplete == null) {
            throw new IllegalArgumentException("onComplete can not be null");
        }

        return subscribe(new Subscriber<T>() {

            @Override
            public final void onCompleted() {
                onComplete.call();
            }

            @Override
            public final void onError(Throwable e) {
                onError.call(e);
            }

            @Override
            public final void onNext(T args) {
                onNext.call(args);
            }

        });
    }
```

如果只关心onNext(),即之上的deffer实例可以写成如下形式
```java
        Observable.defer(() -> Observable.just(System.currentTimeMillis() + "")).subscribe(s -> Logger.d(s));
```

## 线程控制Scheduler

*  在不指定线程的情况下， RxJava 遵循的是线程不变的原则，
      即：在哪个线程调用 subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到 Scheduler （调度器）。
      
*     RxJava内置Scheduler

      1. Schedulers.immediate(): 默认的 Scheduler,当前线程运行，即不指定线程。
      2. Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
      3. Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
      和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。
      4. Schedulers.computation(): 计算所使用的 Scheduler。
      5. Schedulers.from(executor):	使用指定的Executor作为调度器
      这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。
      6. Schedulers.trampoline():当其它排队的任务完成后，在当前线程排队开始执行
      7. AndroidSchedulers.mainThread():它指定的操作将在 Android 主线程运行。
      
*     注意：
      不要把计算工作放在 io() 中，可以避免创建不必要的线程。
      不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
      subscribeOn(): 指定 subscribe() 所发生的线程,即Observable.OnSubscribe 被激活时所处的线程，或者叫做事件产生的线程。
      observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
    
Scheduler实例：      
 
```java
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
```

## Interval 
Interval所创建的Observable对象会从0开始，每隔固定的时间发射一个整数。需要注意的是这个对象是运行在computation Scheduler, 如果涉及到UI操作，需要切换到主线程执行
它按固定的时间间隔发射一个无限递增的整数序列

实例：
```java
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
```

打印结果：interval:0，interval:1，interval:2...

## Repeat 
Repeat会将一个Observable对象重复发射，我们可以指定其发射的次数

实例：

```java
private void repeat() {
        Observable.just(1, 2, 3, 4, 5).repeat(5).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> Logger.d(integer + ""));
    }
```

打印结果：会重复打印5次 1，2，3，4，5

## Timer 
Timer会在指定时间后发射一个，注意其也是运行在computation Scheduler

```java
private void timer() {
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(l -> Logger.d(l + ""));
    }
```

隔3秒之后会在logcat打印一个0

## Empty/Never/Throw

- Empty
创建一个不发射任何数据但是正常终止的Observable
- Never
创建一个不发射数据也不终止的Observable
- Throw
创建一个不发射数据以一个错误终止的Observable,需要传递一个Throwable参数

```java
public void error() {
        Observable.error(new Throwable("error!")).
                observeOn(AndroidSchedulers.mainThread(), true).
                subscribe(this::logger);
    }
```

参考：RxJava操作符（一）Creating Observables