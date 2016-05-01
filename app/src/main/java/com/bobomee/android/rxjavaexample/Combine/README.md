## 概述

RxJava 中的组合函数可以同时处理多个Observables来创建我们想要的Observable。组合操作符包含如下几种

## Merge

merge()将2-9个Observables合并到一个Observable中进行发射。
Merge可能会让合并的Observables发射的数据交错（如果想要没有交错，可以使用concat操作符）。
任何一个Observable发出onError的时候，onError通知会被立即传递给观察者，而且会终止合并后的Observable。
如果想让onError发生在合并后的Observable所有的数据发射完成之后，可以使用MergeDelayError

除了传递多个Observable，merge还可以传递一个Observable列表List，数组，
甚至是一个发射Observable序列的Observable，merge将合并它们的输出作为单个Observable的输出：

实例：
```java
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
```

打印结果：

```java
Next: 1
Next: 3
Next: 5
Next: 2
Next: 4
Next: 6
Sequence complete.
```

## Zip

Zip操作符将2-9个Observable发射的数据按 顺序 结合两个或多个Observables发射的数据项，每个数据只能组合一次，而且都是有序的。

它只发射与发射数据项最少的那个Observable一样多的数据。Rxjava实现了zip和zipWith两个操作符.

具体的结合方式由 第三个参数决定。
zip的最后一个参数接受每个Observable发射的一项数据，返回被压缩后的数据，
它可以接受一到九个参数：一个Observable序列，或者一些发射Observable的Observables。

实例：

- zipwith:

```java
    private Observable<String> createObserver(int index) {
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
        createObserver(2).zipWith(createObserver(3), new Func2<String, String, String>() {
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

```

打印结果：

>emitted:2-1
emitted:3-1
zipWith:2-1-3-1

>emitted:2-2
emitted:3-2
zipWith:2-2-3-2

emitted:3-3

- zip:

```java
    public void zip() {
        Observable
                .zip(createObserver(2), createObserver(3), createObserver(4), new Func3<String, String, String, String>() {
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
```

打印结果：

>emitted:2-1
emitted:3-1
emitted:4-1
zip:2-1-3-1-4-1

>emitted:2-2
emitted:3-2
emitted:4-2
zip:2-2-3-2-4-2

>emitted:3-3
emitted:4-3
emitted:4-4

最终都发射出了两个数据，因为createObserver(2)所创建的Observable只会发射两个数据，所以其他Observable多余发射的数据都被丢弃了。

## Join 

RxJava的join()函数基于时间窗口将两个Observables发射的数据结合在一起,
每个Observable在自己的时间窗口内都有有效的，都可以拿来组合。

Rxjava还实现了groupJoin，基本和join相同，只是最后组合函数的参数不同。

Join(Observable,Func1,Func1,Func2) 参数说明：

- 源Observable所要组合的目标Observable
- 一个函数，接收从源Observable发射来的数据，并返回一个Observable，这个Observable的生命周期决定了源Observable发射出来数据的有效期
- 一个函数，接收从目标Observable发射来的数据，并返回一个Observable，这个Observable的生命周期决定了目标Observable发射出来数据的有效期
- 一个函数，接收从源Observable和目标Observable发射来的数据，并返回最终组合完的数据。

实例：

```java
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
```

打印结果：

>join:Left-Right-1 -->隔1s
join:Left-Right-2 -->隔1s
join:Left-Right-3

groupJoin(Observable,Func1,Func1,Func2):第三个函数的参数：原Observable发射来的数据，要组合的目标Observable

实例：

```java
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
```

打印结果：

>groupJoin:Left-Right-1 -->隔1s
 groupJoin:Left-Right-2 -->隔1s
 groupJoin:Left-Right-3
  
两个结果一致。原Observable发射了一个有效期为3s的数据，目标Observable每1s发射一个有效期为2s的数据，总共4个。
但是最终的组合结果也只有3个数据。

## combineLatest

combineLatest接受二到九个Observable作为参数，或者单个Observables列表作为参数。它默认不在任何特定的调度器上执行。
RxJava的combineLatest()函数有点像zip()函数的特殊形式,zip()作用于最近未打包的两个Observables。相反，combineLatest()作用于最近发射的数据项.

zip 中只有当原始的Observable中的每一个都发射了一条数据时zip才发射数据。
CombineLatest则在原始的Observable中任意一个发射了数据时发射一条数据。

使用实例：

```java
public void combineLatest() {
        Observable.combineLatest(createCombineLatest(1), createCombineLatest(2), (num1, num2) -> {
            logger("left:" + num1 + " right:" + num2);
            return num1 + num2;
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                logger("combineList:" + integer);
            }
        });
    }
```

打印结果：

>left:1 right:2
CombineLatest:3
left:2 right:2
CombineLatest:4
left:2 right:4
CombineLatest:6


Rxjava实现CombineLast操作符可以让我们直接将组装的Observable作为参数传值，也可以将所有的Observable装在一个List里面穿进去。

使用实例：

```java
List<Observable<Integer>> list = new ArrayList<>();

    public void combineList() {
        for (int i = 1; i < 5; i++) {
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
                logger("CombineLatest:" + integer);
            }
        });
    }
```

打印结果：

>1
2
combineList:3
2
2
combineList:4
2
4
combineList:6

## Switch 

将一个发射多个Observables的Observable转换成另一个单独的Observable，后者发射那些Observables最近发射的数据项

switch操作符在Rxjava上的实现为switchOnNext.

用来将一个发射多个小Observable的源Observable转化为一个Observable，
然后发射这多个小Observable所发射的数据。
当原始Observable发射了一个新的Observable时（不是这个新的Observable发射了一条数据时），它将取消订阅之前的那个Observable。
这意味着，在后来那个Observable产生之后到它开始发射数据之前的这段时间里，前一个Observable发射的数据将被丢弃

实例代码：

```java
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
```

打印结果：

>switch:1-1
switch:1-2
switch:2-1
switch:2-2
switch:2-3
switch:2-4

从打印结果看，第一个小Observable只发射出了两个数据，
第二个小Observable就被源Observable发射出来了，所以 *第一个* 接下来的两个数据被丢弃。

## StartWith

StartWith操作符会在源Observable发射的数据前面插上一些数据。

startWith可接受一个Iterable或者多个Observable作为函数的参数。


实例代码：

```java
public void startWith() {
        Observable.just(1, 2, 3).startWith(-1, 0)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger("startWith:" + integer);
                    }
                });
    }
```

打印结果：

>startWith:-1
startWith:0
startWith:1
startWith:2
startWith:3

可以看到-1和0插入到发射序列的前面

使用Demo：[Combining.java](https://github.com/BoBoMEe/RxJavaLearn/blob/master/app/src/main/java/com/bobomee/android/rxjavaexample/Combine)


参考：[ReactiveX中文翻译文档](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Combining-Observables.html)
[RxJava部分操作符介绍 ](http://mushuichuan.com/tags/RxJava/)
[RxJava开发精要6 - 组合Observables]([RxJava部分操作符介绍 ](http://mushuichuan.com/tags/RxJava/))