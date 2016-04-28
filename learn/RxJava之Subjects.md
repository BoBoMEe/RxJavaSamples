## Subjects 

## 概述
Subject这个对象既是Observable又是Observer，我会把Subject想象成一个管道：从一端把数据注入，结果就会从另一端输出。
因为它是一个Observer，它可以订阅一个或多个Observable；
又因为它是一个Observable，它可以转发它收到(Observe)的数据，也可以发射新的数据。

## Subject的种类 

一共有四种类型的Subject,分别为AsyncSubject、BehaviorSubject、PublishSubject和ReplaySubject。

### AsyncSubject

AsyncSubject仅释放Observable释放的最后一个数据，并且仅在Observable完成之后。
如果原始Observable没有发射任何值，AsyncObject也不发射任何值.
然而如果当Observable因为异常而终止，AsyncSubject将不会释放任何数据，但是会向Observer传递一个异常通知。

```java
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
```
打印结果：
>
 Observable onNext:0
 Observable onNext:1
 Observable onNext:2
 Observable onNext:3
 Observable onNext:4
 Observable Completed
 subject : Observable onNext:4
 subject : Observable Completed


### BehaviorSubject

当Observer订阅了一个BehaviorSubject，它一开始就会释放Observable最近释放的一个数据对象，
当还没有任何数据释放时，它则是一个默认值。
接下来就会释放Observable释放的所有数据。
如果Observable因异常终止，BehaviorSubject将不会向后续的Observer释放数据，但是会向Observer传递一个异常通知。

```java
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
```
打印结果：
>1
10
11
12
13
14
Observable completed


### PublishSubject

PublishSubject仅会向Observer释放在订阅之后Observable释放的数据。
PublishSubject可能会一创建完成就立刻开始发射数据（除非你可以阻止它发生），
因此这里有一个风险：在Subject被创建后到有观察者订阅它之前这个时间段内，
一个或多个数据可能会丢失。如果要确保来自原始Observable的所有数据都被分发，
你需要这样做：或者使用Create创建那个Observable以便手动给它引入"冷"Observable的行为（当所有观察者都已经订阅时才开始发射数据），
或者改用ReplaySubject。

```java
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
```

打印结果：
> Observable onNext:0
 subject : Observable onNext:true
  Observable onNext:1
 subject : Observable onNext:false
  Observable onNext:2
 subject : Observable onNext:true
  Observable onNext:3
 subject : Observable onNext:false
  Observable onNext:4
 subject : Observable onNext:true
 Observable Completed
 subject : Observable Completed
 

### ReplaySubject

不管Observer何时订阅ReplaySubject，ReplaySubject会向所有Observer释放Observable释放过的数据。
有不同类型的ReplaySubject，它们是用来限定Replay的范围，例如设定Buffer的具体大小，或者设定具体的时间范围。
如果使用ReplaySubject作为Observer，注意不要在多个线程中调用onNext、onComplete和onError方法，
因为这会导致顺序错乱，这个是违反了Observer规则的。

```java
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
```

打印结果：
>1
1
2
2
3
3
4
4
5
5
observer1:Observable completed
observer2:Observable completed





 