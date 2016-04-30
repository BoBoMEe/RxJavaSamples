## 概述 

过滤操作符用于过滤和选择Observable发射的数据序列，让Observable只返回满足我们条件的数据。

## Debounce 

Debounce会过滤掉发射速率过快的数据项，相当于限流，但是需要注意的是debounce过滤掉的数据会被丢弃掉。
如果在一个指定的时间间隔过去了仍旧没有发射一个，那么它将发射最后的那个。
RxJava将这个操作符实现为throttleWithTimeout和debounce.

简单粗暴的说法：当N个结点发生的时间太靠近（即发生的时间差小于设定的值T），debounce就会自动过滤掉前N-1个结点。
场景：比如EidtText输入联想，可以使用debounce减少频繁的网络请求。避免每输入（删除）一个字就做一次联想。
和switchMap结合使用效果更佳，一个用于取消上次请求，一个用于节流。
  
### throttleWithTimeOut

通过时间来限流，源Observable每次发射出来一个数据后就会进行计时，
如果在设定好的时间结束前源Observable有新的数据发射出来，这个数据就会被丢弃，同时重新开始计时。
如果每次都是在计时结束前发射数据，那么这个限流就会走向极端：只会发射最后一个数据。
默认在computation调度器上执行

实例：
```java
public void throttleWithTimeout() {
        Subscription subscribe = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(i);
                    }
                    int sleep = 100;
                    if (i % 3 == 0) {
                        sleep = 300;
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
                .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> logger("throttleWithTimeout:" + i));
        addSubscription(subscribe);

```
打印结果：

>throttleWithTimeout:0
throttleWithTimeout:3
throttleWithTimeout:6
throttleWithTimeout:9

结果分析：每隔100毫秒发射一个数据，当要发射的数据是3的倍数的时候，下一个数据就延迟到300毫秒再发射
即:0 -300ms-> 1 -100ms-> 2 -100ms-> 3 ..
设定过滤时间为200ms，则1，2都被过滤丢弃。

### deounce

不仅可以使用时间来进行过滤，还可以根据一个函数来进行限流。
这个函数的返回值是一个临时Observable，
如果源Observable在发射一个新的数据的时候，
上一个数据根据函数所生成的临时Observable还没有结束，那么上一个数据就会被过滤掉。
 
值得注意的是，如果源Observable产生的最后一个结果后在规定的时间间隔内调用了onCompleted，
那么通过debounce操作符也会把这个结果提交给订阅者。

```java
public void debounce() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).debounce(integer -> {
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                //如果%2==0，则发射数据并调用了onCompleted结束，则不会被丢弃
                    if (integer % 2 == 0 && !subscriber.isUnsubscribed()) {
                        subscriber.onNext(integer);
                        subscriber.onCompleted();
                    }
                }
            });
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        logger("debounce:" + integer);
                    }
                });
    }
```

打印结果：

>debounce:2
debounce:4
debounce:6
debounce:8
debounce:9

由结果可知，9的打印证明默认调用了onCompleted

 
 
## Distinct 
Distinct操作符的用处就是用来去重，只允许还没有发射过的数据项通过
distinctUntilChanged和这个函数功能类似，是去掉连续重复的数据

实例：

```java
public void distinct(){
        Observable.just(1, 2, 1, 1, 2, 3)
                .distinct()
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

>Next: 1
 Next: 2
 Next: 3
 Sequence complete.
 
```java
public void distinctUntilChangedObserver(){
        Observable.just(1, 2, 3, 3, 3, 1, 2, 3, 3)
                .distinctUntilChanged()
                .subscribe(integer -> logger("UntilChanged:"+integer));
    }
```

打印结果：

>UntilChanged: 1
 UntilChanged: 2
 UntilChanged: 3
 UntilChanged: 1
  UntilChanged: 2
  UntilChanged: 3
  
  
## ElementAt 

从字面意思来看，ElementAt只会返回指定位置的数据。其相关方法有elementAtOrDefault(int,T)，可以允许默认值

实例：

```java
public void elementAt(){
        Observable.just(0, 1, 2, 3, 4, 5).elementAt(2)
                .subscribe(i -> logger("elementAt:" + i));
    }
```

打印结果：
>elementAt:2

## Filter 

允许传入一个Func，通过的数据才会被发射。
特殊形式ofType(Class)：Observable只返回指定类型的数据。

实例：
```java
public void filter() {
        Observable.just(1, 2, 3, 4, 5)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer item) {
                        return (item < 4);
                    }
                }).subscribe(new Subscriber<Integer>() {
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
>Next: 1
Next: 2
Next: 3
Sequence complete.


## First、Last 

First返回满足条件的第一条数据.被实现为first，firstOrDefault和takeFirst。
takeFist会返回一个空的Observable（不调用onNext()但是会调用onCompleted）。

Last操作符只返回最后一条满足条件的数据，被实现为last和lastOrDefault。
如果获取不到数据，则会抛出NoSuchElementException异常

First和Last 都没有实现为一个返回Observable的过滤操作符，
而是一个在当时就发射原始Observable指定数据项的阻塞函数。如果需要的是过滤操作符，
可以使用Take(1)、ElementAt(0)或者TakeLast(1)，TakeLast(Func)

如果不想立即返回Observable，而是需要阻塞并返回值，可以使用BlockingObservable，
通过Observable.toBlocking或者BlockingObservable.from方法来转化。

实例：
```java
public void first() {
        BlockingObservable<Integer> integerBlockingObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!subscriber.isUnsubscribed()) {
                        logger("onNext:" + i);
                        subscriber.onNext(i);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).toBlocking();


        Integer first = integerBlockingObservable.first(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 3;
            }
        });

        logger(first);
    }
```

2s后打印了：<-- 阻塞了，知道大于3的数据发射出来
>onNext:0
onNext:1
onNext:2
onNext:3
onNext:4
4

takeLast实例：

```java
public void takeLast() {
        Observable.just(1, 2, 3, 4, 5, 6, 7).takeLast(2)
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

>Next: 6 
 Next: 7 
 Sequence complete.

## Skip、Take 
  
Skip操作符将源Observable发射的数据过滤掉前n项，而Take操作符则只取前n项

相关操作符：TakeLast：发射Observable发射的最后N项数据，
takeLastBuffer：最后N项数据收集到list再发射

SkipLast：忽略Observable'发射的后N项数据，只保留前面的数据。
skipLast操作符提交满足条件的结果给订阅者存在延迟效果


实例：

```java
 public void skip(){
        Observable.just(0, 1, 2, 3, 4, 5).skip(2).subscribe(i -> logger("Skip:" + i));
    }

    public void take(){
        Observable.just(0, 1, 2, 3, 4, 5).take(2).subscribe(i -> logger("Take:" + i));
    }
```

打印结果：

>Skip:2
Skip:3
Skip:4
Skip:5
Take:0
Take:1

## Sample、ThrottleFirst 
Sample操作符会定时地发射源Observable最近发射的数据，其他的都会被过滤掉。
RxJava将这个操作符实现为sample和throttleLast。


而ThrottleFirst操作符则会定期发射这个时间段里源Observable发射的第一个数据
这两个操作符都在computation调度器上执行。

实例：

```java
private Observable<Integer> createObserver() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });
    }
    
    public void sample() {
            createObserver().sample(1000, TimeUnit.MILLISECONDS)
            .subscribe(i -> logger("sample:" + i));
        }
    
    public void throttleFirst() {
            createObserver().throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe(i -> logger("throttleFirst:" + i));
        }
```

打印结果：
>
sample:3
sample:8
sample:13
sample:18
throttleFirst:0
throttleFirst:5
throttleFirst:10
throttleFirst:15

其中sample操作符会每隔5个数字发射出一个数据来，
而throttleFirst则会每隔5个数据发射第一个数据。


## ignoreElements 
ignoreElements操作符忽略所有源Observable产生的结果，只把Observable的onCompleted和onError事件通知给订阅者。
ignoreElements操作符适用于不太关心Observable产生的结果，只是在Observable结束时(onCompleted)或者出现错误时能够收到通知。

实例：
```java
 public void ignoreElements(){
        Observable.just(1,2,3,4,5,6,7,8).ignoreElements()
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
>Sequence complete.


使用Demo：[Filtering.java](https://github.com/BoBoMEe/RxJavaLearn/blob/master/app/src/main/java/com/bobomee/android/rxjavaexample/ui/Filtering.java)

参考：
[ReactiveX文档中文翻译](https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Filtering-Observables.html)
[RxJava操作符（三）Filtering](http://mushuichuan.com/2015/12/11/rxjava-operator-3/)
[Android RxJava使用介绍（三） RxJava的操作符](http://blog.csdn.net/job_hesc/article/details/46495281)