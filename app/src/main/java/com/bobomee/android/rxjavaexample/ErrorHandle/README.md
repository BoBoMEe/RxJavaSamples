## 概述

Rxjava中，在错误出现的时候就会调用Subscriber的onError方法将错误分发出去，由Subscriber自己来处理错误。
但是如果每个Subscriber都处理一遍的话，工作量就会有点大了，这时候可以使用Error handling相关的操作符来集中统一地处理错误。
RxJava中错误处理的操作符为 Catch和 Retry。

## Catch 

类似于java 中的try/catch，拦截onError的调用，让Observable不会因为错误的产生而终止。
Rxjava中，将这个操作符实现为3个操作符。

首先定义会出错的Observable：

```java
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
```

### OnErrorReturn 

当发生错误的时候，让Observable发射一个预先定义好的数据并正常地终止

onErrorReturn方法 返回一个镜像原有Observable行为的新Observable
会忽略前者的onError调用，不会将错误传递给观察者，而是发射一个特殊的项并调用观察者的onCompleted方法。

实例代码：

```java
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
```

打印结果：

>onErrorReturn-onNext:onNext:1
onErrorReturn-onNext:onNext:2
onErrorReturn-onNext:onErrorReturn
onErrorReturn-onCompleted

>onErrorReturn在错误发生的时候继续发射了提前定义好的数据并正常结束Observable

### OnErrorResume 

当发生错误的时候，由另外一个Observable来代替当前的Observable并继续发射数据

onErrorResumeNext方法返回一个镜像原有Observable行为的新Observable


实例代码：

```java
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
```

打印结果：

>onErrorResume-onNext:onNext:1
onErrorResume-onNext:onNext:2
onErrorResume-onNext:7
onErrorResume-onNext:8
onErrorResume-onNext:9
onErrorResume-onCompleted

>onErrorResume在错误发生后,另一个 继续发射另外的数据7/8/9，然后正常结束了Observable。

### OnExceptionResumeNext

类似于OnErrorResume,不同之处在于其会对onError抛出的数据类型做判断，
如果是Exception，也会使用另外一个Observable代替原Observable继续发射数据，否则会将错误分发给Subscriber。

- Exception类型：

```java
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
```

打印结果：

>onException-true-onNext:onNext:1
onException-true-onNext:onNext:2
onException-true-onNext:7
onException-true-onNext:8
onException-true-onNext:9
onException-true-onCompleted

>Exception时，发生错误，则继续发射另外一个Observable的数据并正常结束

- Throwable类型：

```java
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
```

打印结果：

>onException-false-onNext:onNext:1
onException-false-onNext:onNext:2
onException-false-onError:Throw error

>Throwable类型数据时，错误直接分发到了Subscriber，然后结束

## Retry 

原始Observable遇到错误，重新订阅它期望它能正常终止.

Retry操作符不会将原始Observable的onError通知传递给观察者，它会订阅这个Observable，再给它一次机会无错误地完成它的数据序列。
Retry总是传递onNext通知给观察者，由于重新订阅，可能会造成数据项重复.
这个函数返回一个布尔值，如果返回true，retry应该再次订阅和镜像原始的Observable，
如果返回false，retry会将最新的一个onError通知传递给它的观察者。

retry(long):指定最多重新订阅的次数，如果次数超了，它不会尝试再次订阅，它会把最新的一个onError通知传递给它的观察者。

Rxjava还实现了RetryWhen操作符。和repeatWhen类似，但是一个是重复订阅(触发onComplete)，一个是错误后重复订阅(触发onError())。

当错误发生时，retryWhen会接收onError的throwable作为参数，
并根据定义好的函数返回一个Observable，如果这个Observable发射一个数据，就会重新订阅,
否则，就将这个通知传递给观察者然后终止。

实例代码：

### retry

```java
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
```

打印结果：

>retry-onNext:onNext:1
retry-onNext:onNext:2
retry-onNext:onNext:1
retry-onNext:onNext:2
retry-onNext:onNext:1
retry-onNext:onNext:2
retry-onError:Throw error

>在尝试了几次还是产生错误后，retry会将错误分发给观察者

### retryWhen

```java
public void retryWhenObserver() {
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
```

打印结果：

>subscribing
 delay retry by 1 second(s)
 subscribing
 delay retry by 2 second(s)
 subscribing
 delay retry by 3 second(s)
 subscribing

>在尝试了几次还是产生错误后，retryWhen会正常结束，并不会将错误分发出去。

使用Demo：
[ErrorHandling.java](https://github.com/BoBoMEe/RxJavaLearn/blob/master/app/src/main/java/com/bobomee/android/rxjavaexample/ErrorHandle)

参考：
[ReactiveX中文翻译文档](https://mcxiaoke.gitbooks.io/rxdocs/content/index.html)
[RxJava部分操作符介绍 ](http://mushuichuan.com/tags/RxJava/) 

