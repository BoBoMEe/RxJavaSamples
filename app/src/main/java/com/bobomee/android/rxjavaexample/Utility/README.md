## 概述

辅助操作符就像工具一样，可以让我们更加方便的处理Observable。

## Delay

延迟一段指定的时间再发射来自Observable的发射物

Delay在RxJava中的实现为
delay：延时发射,默认在computation调度器上执行
delaySubscription：延时注册Subscriber,默认不在任何特定的调度器上执行。

注意：delay不会延迟onError通知，它会立即将这个通知传递给订阅者，同时丢弃任何待发射的onNext通知。
然而它会延迟一个onCompleted通知

实例：

```java
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
```

打印结果：

2s后：
>delay:2
delay:2

2s后：
>delaySubscription:0
delaySubscription:0

## Do 

用于给Observable的生命周期的各个阶段加上回调监听，Rxjava实现了很多的doxxx操作符。

- DoOnEach: Observable每发射一个数据的时候都会触发的回调，包括onError和onCompleted。
- DoOnNext: 只有onNext的时候才会被触发.
- doOnSubscribe: Subscriber进行订阅的时候触发,
- doOnUnSubscribe: Subscriber进行反订阅的时候触发,通过OnError或者OnCompleted结束的时候，会反订阅所有的Subscriber。
- DoOnError: OnError发生的时候触发回调
- DoOnComplete: OnCompleted发生的时候触发回调
- DoOnTerminate: 在Observable结束前触发回调，无论是正常还是异
- finallyDo: 在Observable结束后触发回调，无论是正常还是异常终止

## Meterialize\DeMeterialize

Meterialize操作符将OnNext/OnError/OnComplete都转化为一个Notification对象并按照原来的顺序发射出来
DeMeterialize的作用则和Meterialize相反.Notification是一个javaBean

实例：

```java
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
```

打印结果：

>kind:OnNext--value:1
kind:OnNext--value:2
kind:OnNext--value:3
kind:OnCompleted--value:null

>meterialize:1
meterialize:2
meterialize:3

## SubscribOn\ObserverOn 

用于线程切换，ObserverOn用来指定观察者所运行的线程，SubscribOn用来指定Observable所运行的线程

## TimeInterval\TimeStamp 

TimeInterval会拦截发射出来的数据，取代为前后两个发射两个数据的间隔时间。
对于第一个发射的数据，其时间间隔为订阅后到首次发射的间隔。

TimeStamp会将每个数据项给重新包装一下，加上了一个时间戳来标明每次发射的时间

- TimeInterval:

```java
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
```

打印结果：

>onNext:1001
onNext:1012
onNext:1042
onNext:1026
onCompleted!

- TimeStamp:

```java
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
```

打印结果：

>onNext:1462165547361
onNext:1462165548397
onNext:1462165549426
onNext:1462165550440
onCompleted!


## Timeout 

给Observable加上超时时间，每发射一个数据后就重置计时器，
当超过预定的时间还没有发射下一个数据，就抛出一个超时的异常。

实例：

```java
 public void timeOut() {
        createTimeOutObserver().timeout(200, TimeUnit.SECONDS)
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
        createTimeOutObserver().timeout(200, TimeUnit.SECONDS, Observable.just(5, 6))
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger(integer);
                    }
                });
    }
```

打印结果：

>timeOut:0
onError

>0
5
6
可以看到5，6在timeOut后还是被发射出来了

## Using 

创建一个只在Observable生命周期内存在的一次性资源

using(Func0,Func1,Action1)参数：
 一个用户创建一次性资源的工厂函数
 一个用于创建Observable的工厂函数
 一个用于释放资源的函数
 
使用实例：

```java
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
```
 
打印结果：
 
>create animal
animal eat
animal eat
animal eat
animal eat
animal eat
onNext0
onCompleted
animal released

##Serialize
  
强制一个Observable连续调用并保证行为正确和同步。
Observable可以异步(从不同的线程)调用观察者的方法，
可能会出现在onNext之前尝试调用onCompleted或onError，
或者同时调用onNext，默认不在任何特定的调度器上执行。

## TO 

将Observable转换为另一个对象或数据结构

getIterator：用于BlockingObservable，
toFuture：也是用于用于BlockingObservable，将Observable转换为一个返回单个数据项的Future
toIterable：将Observable转换为一个Iterable
toList：发射多项数据的Observable会为每一项数据调用onNext方法。
你可以用toList操作符改变这个行为，让Observable将多项数据组合成一个List，然后调用一次onNext方法传递整个列表。
toMap：收集原始Observable发射的所有数据项到一个Map（默认是HashMap）然后发射这个Map。你可以提供一个用于生成Map的Key的函数
toMultiMap：类似toMap，不同的是，它生成的这个Map同时还是一个ArrayList
toSortedList：类似toList，不同的是，它会对产生的列表排序，默认是自然升序(需要实现Comparable接口)
nest：将一个Observable转换为一个发射这个Observable的Observable。
       

使用Demo：
[Utility.java](https://github.com/BoBoMEe/RxJavaLearn/blob/master/app/src/main/java/com/bobomee/android/rxjavaexample/Utility)

参考：
[ReactiveX中文翻译文档](https://mcxiaoke.gitbooks.io/rxdocs/content/index.html)
[RxJava部分操作符介绍 ](http://mushuichuan.com/tags/RxJava/) 

